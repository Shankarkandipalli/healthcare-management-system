package com.healthcare_management_system.consultation.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultationDTO {
    private Long id;
    private Long AppointmentId;
    private String consultationDate;
    private String consultationTime;
    private String subjectiveNote;
    private String observationFinding;
    private String assessment;
    private String plan;
}
