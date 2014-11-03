package com.acme.todo;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.acme.todo.model.Task;

/**
 * This class is used to list, insert, update or delete Task object.
 * 
 * @author tqvarnst
 * 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TaskService {

	@PersistenceContext
	EntityManager em;

	@Inject
	UserService userService;

	Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This methods should return the users all tasks
	 * code.
	 * 
	 * @return
	 * 
	 */
	public List<Task> findAll() {
		
		List<Task> tasks = userService.getCurrentUser().getTasks();

		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				if (o1.isDone() == o2.isDone()) {
					return o2.getCreatedOn().compareTo(o1.getCreatedOn());
				} else if (o1.isDone()) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		return tasks;
	}

	/**
	 * This method persists a new Task instance
	 * 
	 * @param task
	 * 
	 *            FIXME: Insert the new object into the cache as well
	 */
	public void insert(Task task) {
		if (task.getCreatedOn() == null) {
			task.setCreatedOn(new Date());
		}
		task.setOwner(userService.getCurrentUser());
		em.persist(task);
	}


	/**
	 * This method persists an existing Task instance
	 * 
	 * @param task
	 * 
	 *            FIXME: Add implementation to also update the task object in the cache
	 */
	public void update(Task task) {
		em.createQuery("UPDATE Task t SET t.done=:done, t.createdOn=:createdOn, t.completedOn=:completedOn WHERE t.id=:id")
	 	.setParameter("done", task.isDone())
	 	.setParameter("createdOn", task.getCreatedOn())
	 	.setParameter("completedOn", task.getCompletedOn())
	 	.setParameter("id", task.getId())
	 	.executeUpdate();
	}
	

	/**
	 * This method deletes an Task from the persistence store
	 * 
	 * @param task
	 * 
	 *            DONE: Add implementation to also delete the object from the
	 *            Cache
	 */
	public void delete(Integer taskId) {
		em.createQuery("DELETE FROM Task t WHERE t.id = :id")
        .setParameter("id", taskId)
        .executeUpdate();
	}
}
