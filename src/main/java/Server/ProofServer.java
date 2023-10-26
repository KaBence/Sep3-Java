package Server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ProofServer
{
  public static void main(String[] args)
      throws IOException, InterruptedException
  {
    Server server= ServerBuilder.forPort(1337).addService(new ProofServiceImpl()).build();

    server.start();
    server.awaitTermination();
    System.out.println("Server started");
  }
}
