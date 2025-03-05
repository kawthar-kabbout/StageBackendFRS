package com.stage.dto;

import com.stage.persistans.Activity;
import jakarta.validation.constraints.*;

public class DependanceActivityDTO {

    private Long id;
    private Activity targetActivity;
    private Activity predecessorActivity;
    @NotNull
    private String dependencyType;
}
