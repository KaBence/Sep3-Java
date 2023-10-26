package Client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proof.ProofServiceGrpc;
import proof.*;

public class ProofClient
{
  public static void main(String[] args)
  {
    ManagedChannel managedChannel = ManagedChannelBuilder
        .forAddress("localhost", 1337)
        .usePlaintext()
        .build();
    ProofServiceGrpc.ProofServiceBlockingStub proofStub = ProofServiceGrpc.newBlockingStub(managedChannel);
    GetStringsReq request = GetStringsReq.newBuilder()
        .build();
    GetStringRes response = proofStub.getStrings(request);
    for (String item: response.getOminousList()){
      System.out.println(item);
    }
    managedChannel.shutdown();
  }
}
