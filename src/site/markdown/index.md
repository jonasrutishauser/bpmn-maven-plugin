## BPMN Maven Plugin

The BPMN Maven Plugin searches for all `*.bpmn`, `*.dmn` and `*.cmmn` files under `${basedir}/src` and renders a report of them.

### Goals Overview

The BPMN Maven Plugin only has one goal:

* [bpmn:report](report-mojo.html) Generates a html report which shows the projects BPMN, DMN and CMMN models.

### Usage

General instructions on how to use the BPMN Maven Plugin can be found on the [usage page](usage.html). Some more specific use cases are described in the examples listed below.

If you feel like the plugin is missing a feature or has a defect, you can file a feature request or bug report in the [issue tracker](issue-tracking.html).
When creating a new issue, please provide a comprehensive description of your concern.
Especially for fixing bugs it is crucial that a developer can reproduce your problem.
For this reason, entire debug logs, POMs or most preferably little demo projects attached to the issue are very much appreciated.
Of course, patches are welcome, too.
Contributors can check out the project from the [source repository](source-repository.html).

### Examples

* [Example Report](business-process-models.html)
