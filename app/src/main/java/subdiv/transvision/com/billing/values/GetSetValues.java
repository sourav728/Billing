package subdiv.transvision.com.billing.values;

import java.io.Serializable;

public class GetSetValues implements Serializable{
    private String login_role = "",units="";

    public String getLogin_role() {
        return login_role;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setLogin_role(String login_role) {
        this.login_role = login_role;
    }
}
