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

package info.tol.gocd.task.util;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class TaskResponse {

  private boolean            failure;
  private final List<String> messages = new ArrayList<>();


  public final TaskResponse addMessage(String message) {
    this.messages.add(message);
    return this;
  }

  public final boolean isFailure() {
    return this.failure;
  }

  public final TaskResponse setFailure() {
    this.failure = true;
    return this;
  }

  public final GoPluginApiResponse build() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("success", !this.failure);
    builder.add("message", String.join(", ", this.messages));
    int status = this.failure ? DefaultGoApiResponse.INTERNAL_ERROR : DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
    return new DefaultGoPluginApiResponse(status, builder.build().toString());
  }
}
