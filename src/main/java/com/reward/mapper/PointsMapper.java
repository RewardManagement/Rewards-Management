package com.reward.mapper;
 
import com.reward.dto.PointsDTO;
import com.reward.entity.Points;
 
public class PointsMapper {
 
   
    public static PointsDTO toDTO(Points points) {
        return PointsDTO.builder()
                .id(points.getId())
                .studentName(points.getStudent().getName())
                .pointBalance(points.getPointBalance())
                .totalPoints(points.getTotalPoints())
                .totalSpent(points.getTotalSpent())
                .build();
    }
 
   
    public static Points toEntity(PointsDTO pointsDTO) {
        Points points = new Points();
        points.setPointBalance(pointsDTO.getPointBalance());
        points.setTotalPoints(pointsDTO.getTotalPoints());
        points.setTotalSpent(pointsDTO.getTotalSpent());
        return points;
    }
}