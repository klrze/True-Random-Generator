package entity;

import jakarta.persistence.*; // Communication to database w/o raw SQL commands
import lombok.*; // Automatically writes code behind the scenes

@Getter @Setter // Automatically creates getter and setter methods
@AllArgsConstructor // Creates constructor to fill in every field at once
@NoArgsConstructor // Creates blank constructor (used to build objects from database)
@Entity // Indicates that this class is an entity class
@Table(name = "raw_data") // Indicates exact name of table in database
public class RawData {
    @Id // Marks field as Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment column
    @Column(name = "trialNum") // Links to specific column in table
    private Long trialNum;

    private Long prngNum;
    private Long trngNum;
    private Long qrngNum;
}
