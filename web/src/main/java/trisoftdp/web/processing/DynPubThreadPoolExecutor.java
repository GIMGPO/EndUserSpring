package trisoftdp.web.processing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import trisoftdp.web.core.WebConstants;

public class DynPubThreadPoolExecutor extends ThreadPoolExecutor {	
	public static final Semaphore available = new Semaphore(WebConstants.maxAvailable, true);
	private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);
	private static volatile DynPubThreadPoolExecutor theOnlyInstance = null; 
	private static final AtomicInteger runningJobCount = new AtomicInteger(0);
	
	public static DynPubThreadPoolExecutor getExecutor() {
		if(theOnlyInstance == null) {
			synchronized(DynPubThreadPoolExecutor.class) {
				if(theOnlyInstance == null) //checking again because some time elapsed 
					theOnlyInstance = new DynPubThreadPoolExecutor();
			}
		}
		return theOnlyInstance;
	}
	
	private DynPubThreadPoolExecutor() {
		//TODO It may be more convenient to use one of the Executors factory methods instead of this general purpose constructor.
		/*
		 * Creates a new DynPubThreadPoolExecutor with the given initial parameters and default rejected execution handler.
		 * @param corePoolSize - the number of threads to keep in the pool, even if they are idle.
		 * @param maximumPoolSize - the maximum number of threads to allow in the pool.
		 * @param keepAliveTime - when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating
		 * @param unit  - the time unit for the keepAliveTime argument.
		 * @param workQueue - the queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by the execute method
		 */
		super(WebConstants.corePoolSize,WebConstants.maximumPoolSize, WebConstants.keepAliveTime, WebConstants.unit, workQueue);
	}
 
	public static void incrementRunningJobCount() { runningJobCount.getAndIncrement(); }
	
	public static void decrementRunningJobCount() { runningJobCount.getAndDecrement(); }
	
	public static int getRunningJobCount() { return runningJobCount.get(); }
	
	public static long getAllJobCount() { return (theOnlyInstance == null)? 0: theOnlyInstance.getTaskCount(); }
	
	@Override
	protected void terminated() {
		DynPubThreadPoolExecutor.available.release();
		runningJobCount.getAndDecrement();
		System.out.println("Executor terminated");
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		DynPubThreadPoolExecutor.available.release();
		runningJobCount.getAndDecrement();
		System.out.println("Executor.afterExecute: Job processed");		
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		try {
			DynPubThreadPoolExecutor.available.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		runningJobCount.getAndIncrement();
		System.out.println("Executor.beforeExecute: Job processing started");
	}
		
}
