package org.xamos.rewards.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications")
public class Application {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(name = "client_id")
  private String clientId;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "owner_id")
  private String ownerId;
}
