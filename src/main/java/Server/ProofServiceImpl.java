package Server;

import Utility.DataBase.String.StringData;
import Utility.DataBase.String.StringDataImplementation;
import io.grpc.stub.StreamObserver;
import proof.*;

import java.util.ArrayList;

public class ProofServiceImpl extends ProofServiceGrpc.ProofServiceImplBase
{
  private ArrayList<String > strings;
  private StringData stringData;

  public ProofServiceImpl()
  {
    strings=new ArrayList<>();
    stringData=new StringDataImplementation();

  }

  @Override
  public void getStrings(GetStringsReq req, StreamObserver<GetStringRes> responseObserver){
    strings=stringData.getString();
    GetStringRes res=GetStringRes.newBuilder()
        .addAllOminous(strings)
        .build();

    responseObserver.onNext(res);
    responseObserver.onCompleted();
  }

  @Override
  public void putString(PutStringReq req, StreamObserver<PutStringRes> responseObserver){
    String temp=req.getOminous();

    stringData.addSOmeValueToThisTest(temp);

    PutStringRes res=PutStringRes.newBuilder()
        .setResp("Good Job")
        .build();

    responseObserver.onNext(res);
    responseObserver.onCompleted();
  }
}
