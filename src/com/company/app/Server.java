package com.company.app;

import com.company.model.HraciPlocha1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private static boolean isServer;

    public static boolean isServer()
    {
        return isServer;
    }

    public static void setIsServer(boolean isServer)
    {
        Server.isServer = isServer;
    }

    //region Singleton

    private static Server server = null;

    public static Server getInstance()
    {
        return server;
    }

    public static void setServer(Server server)
    {
        Server.server = server;
    }

    //endregion

    private BufferedReader[] readers;
    private PrintWriter[] writers;

    private Socket[] clients;
    private ServerSocket socket;

    public Server(int pocetHracu, int port) throws IOException
    {
        socket = new ServerSocket(port);

        clients = new Socket[pocetHracu - 1];
        readers = new BufferedReader[pocetHracu - 1];
        writers = new PrintWriter[pocetHracu - 1];
        for (int i = 0; i < pocetHracu - 1; i++)
        {
            clients[i] = socket.accept();

            readers[i] = new BufferedReader(new InputStreamReader(clients[i].getInputStream()));
            writers[i] = new PrintWriter(clients[i].getOutputStream(), true);

            nastavitPoradi(i);
        }
    }

    private void nastavitPoradi(int i)
    {
        writers[i].println(i + 1);
    }

    public void konec()
    {
        try
        {
            for (Socket client : clients)
            {
                client.close();
            }
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void zprava(String[] params)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++)
        {
            sb.append(params[i]);
            if (i < params.length - 1)
            {
                sb.append(".");
            }
        }
        for (int i = 0; i < clients.length; i++)
        {
            writers[i].println(sb);
        }
    }

    public String[] read(int i) throws IOException
    {
        return readers[i].readLine().split("\\.");
    }
}
