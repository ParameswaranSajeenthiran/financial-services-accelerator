package org.wso2.financial.services.accelerator.consent.mgt.endpoint.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;


@JsonTypeName("ConsentStatusUpdateResource")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen",
        date = "2025-03-03T09:27:49.560668411+05:30[Asia/Colombo]", comments = "Generator version: 7.12.0")
public class ConsentRevokeResource implements Serializable {
    private String userID;
    private String reason;

    public ConsentRevokeResource() {
    }

    /**
     *
     **/
    public ConsentRevokeResource userID(String userID) {
        this.userID = userID;
        return this;
    }


    @ApiModelProperty(example = "psu@wso2.com", value = "")
    @JsonProperty("userID")
    public String getUserId() {
        return userID;
    }

    @JsonProperty("userID")
    public void setUserId(String userID) {
        this.userID = userID;
    }





    /**
     *
     **/
    public ConsentRevokeResource reason(String reason) {
        this.reason = reason;
        return this;
    }


    @ApiModelProperty(example = "Revoked by ...", value = "")
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsentRevokeResource consentStatusUpdateResource = (ConsentRevokeResource) o;
        return Objects.equals(this.userID, consentStatusUpdateResource.userID) &&
                Objects.equals(this.reason, consentStatusUpdateResource.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, reason);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ConsentStatusUpdateResource {\n");

        sb.append("    userID: ").append(toIndentedString(userID)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }


}

