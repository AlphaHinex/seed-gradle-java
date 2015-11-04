activiti-multi-outgoing-sequence-flow branch
============================================

Single user task with multi-outgoing sequence flow works fine,
but when the user task is multi-instance, multi-outgoing sequence flows's behaviour is weird. 

How to test
-----------

### Directly run

    $ ./gradlew test

### Import to IDE

#### IDEA

    $ ./gradlew idea

#### Eclipse

    $ ./gradlew eclipse

Sources
-------

* test source: [MultiOutgoingSequenceFlowTest.java](src/test/java/io/github/hinex/alpha/activiti/mosf/MultiOutgoingSequenceFlowTest.java)
* test process definitions:
    1. [MultiOutgoingSequenceFlowTest.testSingleTask.bpmn20.xml](src/test/resources/io/github/hinex/alpha/activiti/mosf/MultiOutgoingSequenceFlowTest.testSingleTask.bpmn20.xml)
    1. [MultiOutgoingSequenceFlowTest.testMultiTasksV1.bpmn20.xml](src/test/resources/io/github/hinex/alpha/activiti/mosf/MultiOutgoingSequenceFlowTest.testMultiTasksV1.bpmn20.xml)
    1. [MultiOutgoingSequenceFlowTest.testMultiTasksV2.bpmn20.xml](src/test/resources/io/github/hinex/alpha/activiti/mosf/MultiOutgoingSequenceFlowTest.testMultiTasksV2.bpmn20.xml)

