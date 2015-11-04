package io.github.hinex.alpha.activiti.mosf;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MultiOutgoingSequenceFlowTest {

    @Autowired
    @Rule
    public ActivitiRule activitiRule;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Test
    @Deployment
    public void testSingleTask() {
        // Instance 1: theStart - flow1 - singleTask - flow3 - theEnd
        // works fine
        String procInstId = runtimeService.startProcessInstanceByKey("mosfSingle").getId();

        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        Map<String, Object> var = new HashMap<String, Object>(1);
        var.put("approveResult", "yes");
        taskService.complete(task.getId(), var);

        assertEquals(0, taskService.createTaskQuery().processInstanceId(procInstId).count());
        assertNull(runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult());

        // Instance 2: theStart - flow1 - singleTask - flow2 - notPass - flow4 - theEnd
        // works fine
        procInstId = runtimeService.startProcessInstanceByKey("mosfSingle").getId();
        task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        var.put("approveResult", "no");
        taskService.complete(task.getId(), var);

        task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        assertEquals("Not Pass", task.getName());

        taskService.complete(task.getId());
        assertNull(runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult());
    }

    @Test
    @Deployment
    public void testMultiTasksV1() {
        List<String> assigneeList = Arrays.asList("kermit", "gonzo", "mispiggy", "fozzie", "bubba");
        String procInstId = runtimeService.startProcessInstanceByKey("mosfMultiV1",
                CollectionUtil.singletonMap("assigneeList", assigneeList)).getId();

        // Expect to flow as: theStart - flow1 - miTasks - flow3 - theEnd
        // but an exception occurs when the last multi-instance completed
        Map<String, Object> var = new HashMap<String, Object>(1);
        var.put("approveResult", "yes");
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        for (Task task : tasks) {
            taskService.complete(task.getId(), var);
        }

        assertEquals(0, taskService.createTaskQuery().processInstanceId(procInstId).count());
        assertNull(runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult());
    }

    @Test
    @Deployment
    public void testMultiTasksV2() {
        // The only difference between v1 and v2 definitions is the sequence of flow2 and flow3, except definition id
        List<String> assigneeList = Arrays.asList("kermit", "gonzo", "mispiggy", "fozzie", "bubba");
        // Instance 1: theStart - flow1 - miTasks - flow2 - notPass - flow4 - theEnd
        // works 'fine'
        String procInstId = runtimeService.startProcessInstanceByKey("mosfMultiV2",
                CollectionUtil.singletonMap("assigneeList", assigneeList)).getId();

        Map<String, Object> var = new HashMap<String, Object>(1);
        var.put("approveResult", "no");
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        for (Task task : tasks) {
            taskService.complete(task.getId(), var);
        }

        Task task = taskService.createTaskQuery().processInstanceId(procInstId).singleResult();
        assertEquals("Not Pass", task.getName());

        taskService.complete(task.getId());
        assertNull(runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult());

        // Instance 2 expect to flow as: theStart - flow1 - miTasks - flow3 - theEnd
        // but still go to 'notPass' task after 'miTasks' completed with appropriate variable value
        procInstId = runtimeService.startProcessInstanceByKey("mosfMultiV2",
                CollectionUtil.singletonMap("assigneeList", assigneeList)).getId();

        var.put("approveResult", "yes");
        tasks = taskService.createTaskQuery().processInstanceId(procInstId).list();
        for (Task t : tasks) {
            taskService.complete(t.getId(), var);
        }

        assertEquals(0, taskService.createTaskQuery().processInstanceId(procInstId).count());
        assertNull(runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult());
    }

}
