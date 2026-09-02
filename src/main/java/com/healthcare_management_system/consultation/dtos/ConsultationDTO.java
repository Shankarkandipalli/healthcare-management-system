package com.healthcare_management_system.consultation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultationDTO {
    private Long id;

    private Long appointmentId;

    private LocalDateTime consultationDate;

    private String subjectiveNotes;

    private String objectiveFindings;

    private String assessment;

    private String plan;
}
