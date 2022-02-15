package in.ripple.user.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "role_access_mapping")
public class RoleAccessMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "role")
    private Long roleId;

    @Column(name = "access")
    private Long access;


}
