/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package cd.go.task.http;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import cd.go.common.request.RequestHandler;
import cd.go.common.task.TaskRequest;
import cd.go.common.task.TaskResponse;
import cd.go.common.util.HttpClient;

/**
 * This message is sent by the GoCD agent to the plugin to execute the task.
 *
 * <pre>
 * {
 *   "config": {
 *     "ftp_server": {
 *       "secure": false,
 *       "value": "ftp.example.com",
 *       "required": true
 *     },
 *     "remote_dir": {
 *       "secure": false,
 *       "value": "/pub/",
 *       "required": true
 *     }
 *   },
 *   "context": {
 *     "workingDirectory": "working-dir",
 *     "environmentVariables": {
 *       "ENV1": "VAL1",
 *       "ENV2": "VAL2"
 *     }
 *   }
 * }
 * </pre>
 */
public class HttpTaskHandler implements RequestHandler {

  private final JobConsoleLogger console;

  /**
   * Constructs an instance of {@link HttpTaskHandler}.
   *
   * @param console
   */
  public HttpTaskHandler(JobConsoleLogger console) {
    this.console = console;
  }

  /**
   * Handles a request and provides a response.
   *
   * @param request
   */
  @Override
  public GoPluginApiResponse handle(GoPluginApiRequest request) {
    TaskRequest task = TaskRequest.of(request);
    String url = task.getConfig().getValue("url");
    String files = task.getConfig().getValue("files");
    // String username = task.getConfig().getValue("username");
    // String password = task.getConfig().getValue("password");
    String destination = task.getConfig().getValue("destination");

    console.printLine("Launching command on: " + task.getWorkingDirectory());
    // console.printEnvironment(task.getEnvironment().toMap());

    File workingDir = new File(task.getWorkingDirectory()).getAbsoluteFile();
    if (destination != null && !destination.trim().isEmpty()) {
      workingDir = new File(workingDir, destination);
      workingDir.mkdirs();
    }

    TaskResponse response = new TaskResponse();
    for (String uri : HttpTaskHandler.getUrls(url, files)) {
      fetchFile(uri, workingDir, response);
    }
    return response.isFailure() ? response.build() : response.addMessage("HTTP Task executed!").build();
  }

  /**
   * Fetch the files.
   * 
   * @param url
   * @param workingDir
   * @param messages
   */
  protected void fetchFile(String url, File workingDir, TaskResponse response) {
    try (HttpClient client = HttpClient.get(url)) {
      // always check HTTP response code first
      if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
        String fileName = getFilename(url, client.getHeader("Content-Disposition"));

        File file = new File(workingDir, fileName);
        client.storeTo(file);

        response.addMessage("File '" + fileName + "' downloaded");
      } else {
        response.addMessage(client.getResponseText());
      }
    } catch (Throwable e) {
      response.addMessage(e.toString());
    }
    response.setFailure();
  }

  /**
   * Get the files to download.
   * 
   * @param config
   */
  private static List<String> getUrls(String url, String files) {
    List<String> urls = new ArrayList<>();
    if (files != null && !files.trim().isEmpty()) {
      if (!url.endsWith("/"))
        url += "/";

      for (String filename : files.split("[,\\n]")) {
        if (!filename.trim().isEmpty()) {
          urls.add(url + filename.trim());
        }
      }
    }

    if (urls.isEmpty()) {
      urls.add(url);
    }
    return urls;
  }

  /**
   * Get the filename.
   * 
   * @param url
   * @param disposition
   */
  private static String getFilename(String url, String disposition) {
    if (disposition == null) {
      // extracts file name from URL
      return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    // extracts file name from header field
    int index = disposition.indexOf("filename=");
    return (index > 0) ? disposition.substring(index + 10, disposition.length() - 1) : "";
  }
}
