package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
class Path {
    @Getter
    @NonNull private LocationName startLocation; // Start location
    @Getter @NonNull private LocationName endLocation; // End location
}
