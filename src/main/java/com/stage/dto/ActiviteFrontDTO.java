package com.stage.dto;

import com.stage.persistans.Employer;
import com.stage.persistans.Machine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiviteFrontDTO {
    private int id;
    private String name;
    private int level;
    private boolean checked;
    private Integer duration;
    private boolean isParent;
    private List<Employer>employers ;
    private List<Machine>machines;
    private int employersNumber;

}
