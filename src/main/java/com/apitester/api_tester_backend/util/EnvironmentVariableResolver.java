package com.apitester.api_tester_backend.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.apitester.api_tester_backend.exception.ApiException;

@Component
public class EnvironmentVariableResolver {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

    // Find {{variable}} and replace it with the corresponding value.
    public String resolve(String value,Map<String, String> variables) {

        if (value == null) {
            return null;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(value);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {

            String variableName = matcher.group(1).trim();

            String variableValue = variables.get(variableName);

            if (variableValue == null) {

                throw new ApiException(
                        "Environment variable not found: "+ variableName, HttpStatus.BAD_REQUEST);
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(variableValue)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }
}
