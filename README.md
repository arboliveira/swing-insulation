Swing Insulation
================

Before you begin
----------------

Run the example application:

    example-numbers.cmd

Then, proceed reading.


The problem
-----------

In Java, everyone who learns to program with Swing eventually
runs into the same mistake. You bind an event to a component,
implements the event to execute a long running operation and
bang: your user interface has just become unresponsive.

Swing makes no effort at all to give a warning or point 
you to the right way: you must send long running operations
to a different thread, because events are invoked from the 
Swing thread itself. They must finish quickly or the screen 
will freeze while the Swing thread is busy, with no chance 
to repaint or respond to further user interaction. 

Sending long running operations to a different thread is not
easy to do right, though. First, sometimes you don't know
beforehand if an operation will take a long time or not. It
is safer to send everything to another thread, for optimal
user interface responsiveness.

But if you start a new thread on every single event, you will 
face race conditions that might render your application 
unpredictable. What if operation B requires operation A to 
finish in order to make sense? Perhaps it's better to place
them in a queue to be consumed sequentially by a single thread,
separate from the Swing thread.

But what if we have operations C and D, which are completely
independent? The application would benefit 
if they were actually performed each one in its own thread.
But what if we have independent long running operations 
E, F, G, H and I? If we create a new thread for each one,
CPU consumption will skyrocket. How can we combine strategies
to reach optimal performance?


The solution
------------

Swing Insulator allows you to seamlessly submit event operations 
to a `java.util.concurrent.Executor` (or as many as you need). 
You can pick one of the handy builders in 
`java.util.concurrent.Executors` if all you 
need is a single thread or a thread pool.

Declare an interface with methods representing operations to
be invoked from user events. Implement the interface in 
an object and use one of the handy builders in
`br.com.arbo.swinginsulation.Insulators` to wrap it inside a proxy. 
Pass the proxy along to the user interface and all method 
invocations will be submitted to the Executor of your choice, 
outside the Swing thread.

There is also the reverse problem: when you
need to provide visual feedback from a long running operation,
all user interface updates should be enqueued onto the Swing
thread. This will guarantee all changes are painted at once, 
avoiding flicker and race conditions. Swing Insulation provides
handy builders for proxies that will seamlessly submit method 
invocations to the Swing thread.


Where to go from here
---------------------

Study the example program
(`br.com.arbo.swinginsulation.examples.numbers.Main`)
or the JUnit test cases to find out how to use Swing Insulation 
in your application.
