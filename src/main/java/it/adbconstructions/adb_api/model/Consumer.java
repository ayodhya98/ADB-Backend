package it.adbconstructions.adb_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Consumer extends BaseUser{

    @OneToMany(mappedBy = "requestedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("requestedUser")
    private List<Request> requests;

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
