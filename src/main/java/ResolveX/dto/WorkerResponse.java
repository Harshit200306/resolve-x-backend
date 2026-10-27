package ResolveX.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerResponse {

    private Long id;
    private String name;
    private String category;
    private Integer experience;
    private Boolean available;
}