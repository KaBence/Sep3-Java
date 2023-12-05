package Server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SepGrpServer
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        Server server= ServerBuilder.forPort(1337).addService(new SepServiceImplementation()).build();

        server.start();
        System.out.println("Server started...");
        server.awaitTermination();
    }
}
