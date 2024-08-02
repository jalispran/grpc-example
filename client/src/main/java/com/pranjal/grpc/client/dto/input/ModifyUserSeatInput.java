package com.pranjal.grpc.client.dto.input;

import com.pranjal.grpc.train.ModifyUserSeatRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModifyUserSeatInput {
    @NotBlank
    private String newSeatSection;
    @NotBlank
    private String newSeatNumber;
    @NotBlank
    private String email;

    public ModifyUserSeatRequest toGrpcModifyUserSeatRequest() {
        return ModifyUserSeatRequest.newBuilder()
                .setNewSeatSection(newSeatSection)
                .setNewSeatNumber(newSeatNumber)
                .setEmail(email)
                .build();
    }
}
