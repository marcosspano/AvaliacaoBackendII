package com.dh.msusers.model;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor()
public class User {

    private String id;
    private String name;
    private String dni;
    private String email;
    private List<Bills> bills;

}
