package com.someluigi.slperiph.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.util.EnumChatFormatting;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.someluigi.slperiph.SLPMod;
import com.someluigi.slperiph.tileentity.TileEntityHTTPD;

import dan200.computer.api.IComputerAccess;

public class SLPHTTPServer implements Container {

    public static Connection connection;
    public static WeakHashMap<Integer, Object> services = new WeakHashMap<Integer, Object>();

    // public static utilWeakList reqs = new utilWeakList();
    public static void start(int port) {

        Container container = new SLPHTTPServer();
        Server server;
        try {
            server = new ContainerServer(container);

            connection = new SocketConnection(server);
            SocketAddress address = new InetSocketAddress(port);

            connection.connect(address);
        } catch (Exception e) {
            System.err
                    .println("Caught exception from SLP HTTP Server in START");
            e.printStackTrace();
            
            SLPMod.httpdStat = EnumChatFormatting.RED + "ERRORED - " + e.getClass().getName() + ": " + e.getMessage();
            
        }

        // connection.close();
    }

    public static void stop() {
        try {
            connection.close();
        } catch (IOException e) {
            System.err.println("Caught exception from SLP HTTP Server in STOP");
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Request req, Response res) {
        try {
            Path p = req.getPath();
            Query qry = req.getQuery();
            PrintStream ps = res.getPrintStream();
            long time = System.currentTimeMillis();
            String service;

            if (p.getSegments().length >= 1) {
                service = p.getSegments()[0];
            } else {
                service = "";
            }

            res.setValue("Content-Type", "text/html");
            res.setValue("Server", "SLPeripherals/1.0 (Simple 4.0)");
            res.setDate("Date", time);
            res.setDate("Last-Modified", time);

            // System.out.println("request" + url);

            try {
                if (Integer.valueOf(service) != null) {
                    int i = Integer.valueOf(service);

                    if (services.get(i) != null) {
                        // put an event and wait for response

                        IComputerAccess ica = (IComputerAccess) ((Object[]) services
                                .get(i))[0];
                        TileEntityHTTPD hd = (TileEntityHTTPD) ((Object[]) services
                                .get(i))[1];

                        hd.reqsw.add(ps);
                        int idx = hd.reqsw.lastIndexOf(ps);

                        // reqs.add("{bl}");
                        // int idx=reqs.lastIndexOf("{bl}");
                        
                        
                        Map<String, String> cookies = new HashMap<String, String>();
                        
                        for (Cookie c: res.getCookies()) {
                            cookies.put(c.getName(), c.getValue());
                        }
                        
                        ica.queueEvent("http_server_request", new Object[] {
                                idx, p.getPath(1), qry, cookies });

                        return;

                        /*
                         * 
                         * for (int ij = 1; ij <= 10; ij++) { Thread.sleep(100);
                         * 
                         * if (! reqs.get(idx).equals("{bl}")) { // request
                         * done. ps.print(reqs.get(idx)); ps.close(); return; }
                         * 
                         * }
                         * 
                         * // request timed out
                         * 
                         * reqs.remove(idx);
                         * 
                         * 
                         * 
                         * ps.print(
                         * "<hr>The computer did not respond after 1 second, request timed out."
                         * ); ps.close(); return;
                         */

                    } else {
                        ps.print("SLP HTTP Server (based on the Simple Framework)<br>"
                                + "The service " + i + " is not online.");
                    }

                }
            } catch (Exception e) {
                ps.print("SLP HTTP Server (based on the Simple Framework)<br>"
                        + "Please go to a service by typing the computer id then a slash, example:<br>"
                        + "http://localhost/4/<br>"
                        + "would go to Computer ID 4's service. - The IDs are assigned by ComputerCraft.<br>"
                        + "(please note 'localhost' depends on your setup. You may have to use a port number too. See the current URL.).<br>(exception encountered)");
            }

            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}