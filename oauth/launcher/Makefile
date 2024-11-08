# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

DEBUG_FLAGS := -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
export JAVA_OPTS := $(DEBUG_FLAGS)

secrets/google/clientId:
	@echo [WARN] $@ missing, OIDC config needs to be created manually

secrets/google/clientSecret:
	@echo [WARN] $@ missing, OIDC config needs to be created manually

secrets/github/clientId:
	@echo [WARN] $@ missing, OAuth config needs to be created manually

secrets/github/clientSecret:
	@echo [WARN] $@ missing, OAuth config needs to be created manually

secrets/encrypt/password:
	mkdir -p secrets/encrypt
	openssl rand  -hex 32 > $@

run: target/dependency/org.apache.sling.feature.launcher/bin/launcher secrets/google/clientId secrets/google/clientSecret secrets/encrypt/password
	@echo '-------------------------------------------------------------------------------------------'
	@echo '[NOTE] Launching application, this will fail if you did not build the project at least once'
	@echo '[NOTE] Remove the launcher folder to throw away local changes'
	@echo '-------------------------------------------------------------------------------------------'
	$< -f target/slingfeature-tmp/feature-app.json -D org.apache.felix.configadmin.plugin.interpolation.secretsdir=secrets
.PHONY=run

target/dependency/org.apache.sling.feature.launcher/bin/launcher:
	mvn package

run-redis:
	docker run -d --name sling-oauth-redis -p 6379:6379 redis:7.4
.PHONY=run-redis

clean:
	rm -rf launcher
.PHONY=clean
