# BPMN Maven Plugin

The [bpmn-maven-plugin](https://jonasrutishauser.github.io/bpmn-maven-plugin/snapshot).
Generates a [maven](https://maven.apache.org) report which shows the projects BPMN, DMN and CMMN models.

[![GNU Lesser General Public License, Version 3, 29 June 2007](https://img.shields.io/github/license/jonasrutishauser/bpmn-maven-plugin.svg?label=License)](http://www.gnu.org/licenses/lgpl-3.0.txt)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.jonasrutishauser/bpmn-maven-plugin.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.jonasrutishauser%22%20a%3A%22bpmn-maven-plugin%22)
[![Build Status](https://img.shields.io/travis/jonasrutishauser/bpmn-maven-plugin/master.svg?label=Build)](https://travis-ci.org/jonasrutishauser/bpmn-maven-plugin)
[![Coverage](https://img.shields.io/codecov/c/github/jonasrutishauser/bpmn-maven-plugin/master.svg?label=Coverage)](https://codecov.io/gh/jonasrutishauser/bpmn-maven-plugin)

## Releasing

* Execute `mvn -B release:clean release:prepare release:perform`
* Deploy site `(cd target/checkout; mvn -P release site-deploy)`
