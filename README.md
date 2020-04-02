# GoCD task plugin for packaging

This is merely a skeleton plugin that plugin developers can fork to get quickly 
started with writing task plugins for GoCD. It works out of the box, but you should change 
it to do something besides executing `curl`.
 
All the documentation is hosted at https://plugin-api.gocd.io/current/tasks.



## Getting started

The http task plugin is used to fetch any resource from a remote HTTP host.

### Configuration

The plugin accepts parameters:

- *url*: a required parameter to define the HTTP location.
- *username*: an optional username, if authentication is required.
- *password*: an optional password, if authentication is required.
- *password*: an optional password, if authentication is required.
- *files*: an optional list of files to fetch, if ommited the url must define a path to a single file 

## Building the code base

To build the jar, run `./gradlew clean test assemble`

## License

```plain
Copyright 2018 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## About the license and releasing your plugin under a different license

The skeleton code in this repository is licensed under the Apache 2.0 license. The license itself specifies the terms
under which derivative works may be distributed (the license also defines derivative works). The Apache 2.0 license is a
permissive open source license that has minimal requirements for downstream licensors/licensees to comply with.

This does not prevent your plugin from being licensed under a different license as long as you comply with the relevant
clauses of the Apache 2.0 license (especially section 4). Typically, you clone this repository and keep the existing
copyright notices. You are free to add your own license and copyright notice to any modifications.

This is not legal advice. Please contact your lawyers if needed.
