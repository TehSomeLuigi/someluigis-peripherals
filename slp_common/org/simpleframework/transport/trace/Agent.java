/*
 * Agent.java October 2012
 *
 * Copyright (C) 2002, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.simpleframework.transport.trace;

import java.nio.channels.SocketChannel;

/**
 * The <code>Agent</code> object represents a tracing agent used to monitor
 * events on a connection. Its primary responsibilities are to create
 * <code>Trace</code> objects that are attached to a specific socket channel.
 * When any event occurs on that channel the trace is notified and can forward
 * the details on to the agent for analysis.
 * <p>
 * An agent implementation must make sure that it does not affect the
 * performance of the server. If there are delays creating a trace or within the
 * trace itself it will have an impact on performance.
 * 
 * @author Niall Gallagher
 * 
 * @see org.simpleframework.transport.trace.Trace
 */
public interface Agent {

    /**
     * This method is used to attach a trace to the specified channel. Attaching
     * a trace basically means associating events from that trace with the
     * specified socket. It ensures that the events from a specific channel can
     * be observed in isolation.
     * 
     * @param channel
     *            this is the channel to associate with the trace
     * 
     * @return this returns a trace associated with the channel
     */
    Trace attach(SocketChannel channel);

    /**
     * This is used to stop the agent and clear all trace information. Stopping
     * the agent is typically done when the server is stopped and is used to
     * free any resources associated with the agent. If an agent does not hold
     * information this method can be ignored.
     */
    void stop();
}