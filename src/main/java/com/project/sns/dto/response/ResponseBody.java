package com.project.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBody<T> {

    private String resultCode;
    private T result;

    public static ResponseBody<Void> error(String errorCode) {
        return new ResponseBody<>(errorCode, null);
    }

    public static <T> ResponseBody<T> success(String resultCode, T result) {
        return new ResponseBody(resultCode, result);
    }

    public String toStream() {
        if(this.result == null){
            return new StringBuilder()
                    .append("{")
                    .append("\"resultCode\" : ").append("\"").append(resultCode).append("\"").append(",")
                    .append("\"result\" : ").append(result)
                    .append("}")
                    .toString();
        }

        return new StringBuilder()
                .append("{")
                .append("\"resultCode\" : ").append("\"").append(resultCode).append("\"").append(",")
                .append("\"result\" : ").append("\"").append(result).append("\"")
                .append("}")
                .toString();
    }

}
