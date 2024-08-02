package com.pranjal.grpc.client.service;

import com.pranjal.grpc.client.config.GrpcChannelManager;
import com.pranjal.grpc.client.dto.input.ModifyUserSeatInput;
import com.pranjal.grpc.client.dto.output.PurchaseTicketOutput;
import com.pranjal.grpc.client.dto.output.UserSeatOutput;
import com.pranjal.grpc.train.GetReceiptRequest;
import com.pranjal.grpc.train.GetUsersBySectionRequest;
import com.pranjal.grpc.train.ModifyUserSeatRequest;
import com.pranjal.grpc.train.PurchaseTicketRequest;
import com.pranjal.grpc.train.PurchaseTicketResponse;
import com.pranjal.grpc.train.RemoveUserRequest;
import com.pranjal.grpc.train.RemoveUserResponse;
import com.pranjal.grpc.train.TrainTicketServiceGrpc;
import com.pranjal.grpc.train.TrainTicketServiceGrpc.TrainTicketServiceBlockingStub;
import com.pranjal.grpc.train.TrainTicketServiceGrpc.TrainTicketServiceFutureStub;
import com.pranjal.grpc.train.TrainTicketServiceGrpc.TrainTicketServiceImplBase;
import com.pranjal.grpc.train.TrainTicketServiceGrpc.TrainTicketServiceStub;
import com.pranjal.grpc.train.UserSeat;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService extends TrainTicketServiceImplBase {

    public static final int ONE_SECOND = 1_000;
    private final GrpcChannelManager channelManager;
    @GrpcClient("target-server")
    private TrainTicketServiceBlockingStub blockingStub;

    public PurchaseTicketOutput purchaseTicket(PurchaseTicketRequest request) {
        PurchaseTicketResponse response = blockingStub.purchaseTicket(request);
        return PurchaseTicketOutput.fromGrpcResponse(response);
    }

    public PurchaseTicketOutput getReceipt(String email) {
        GetReceiptRequest request = GetReceiptRequest.newBuilder()
                .setEmail(email)
                .build();
        PurchaseTicketResponse response = blockingStub.getReceipt(request);
        return PurchaseTicketOutput.fromGrpcResponse(response);
    }

    public List<UserSeatOutput> getSeatsBySection(String sectionName) {
        GetUsersBySectionRequest request = GetUsersBySectionRequest.newBuilder()
                .setSection(sectionName)
                .build();
        Iterator<UserSeat> usersIterator = blockingStub.getUsersBySection(request);
        List<UserSeatOutput> seatList = new ArrayList<>();
        while(usersIterator.hasNext()) {
            UserSeat userSeat = usersIterator.next();
            seatList.add(UserSeatOutput.fromGrpcUserSeat(userSeat));
        }
        return seatList;
    }

    public String removeUser(String email) {
        RemoveUserRequest removeUserRequest = RemoveUserRequest.newBuilder()
                .setEmail(email)
                .build();
        RemoveUserResponse removeUserResponse = blockingStub.removeUser(removeUserRequest);
        return removeUserResponse.getMessage();
    }

    public PurchaseTicketOutput modifyUserSeat(ModifyUserSeatInput input) {
        ModifyUserSeatRequest request = input.toGrpcModifyUserSeatRequest();
        PurchaseTicketResponse response = blockingStub.modifyUserSeat(request);
        return PurchaseTicketOutput.fromGrpcResponse(response);
    }

}
