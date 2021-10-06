package com.company.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client
{
    //region Singleton

    private static Client client = null;

    public static Client getInstance()
    {
        return client;
    }

    public static void setClient(Client client)
    {
        Client.client = client;
    }

    //endregion

    private BufferedReader reader;
    private PrintWriter writer;

    private int poradi;

    public int getPoradi()
    {
        return poradi;
    }

    private Socket socket;

    public Client(String host, int port) throws IOException
    {
        socket = new Socket(host, port);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        poradi = Integer.parseInt(read()[0]);
    }

    public void konec()
    {
        try
        {
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
        writer.println(sb);
    }

    public String[] read() throws IOException
    {
        return reader.readLine().split("\\.");
    }
}
