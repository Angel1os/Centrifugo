package com.angellos.push.utility;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.angellos.push.dto.ResponseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class AppUtils {
    public static ResponseRecord getResponseRecord(String message, HttpStatus status){
        ResponseRecord responseRecord = new ResponseRecord(message, status.value(), null, ZonedDateTime.now());
        return responseRecord;

    }

    public static ResponseRecord getResponseRecord(String message, HttpStatus status, Object data){
        ResponseRecord responseRecord = new ResponseRecord(message, status.value(), data, ZonedDateTime.now());
        return responseRecord;
    }

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PAGE_SORT = "createdAt";
    public static final String DEFAULT_PAGE_SORT_DIR = "desc";

    /**
     * This method is used to generate a pageable to make a paginated request
     * @param params This is a Map that has the page number, size, sortBy and sortDir for the pagination
     * @return
     */
    public static Pageable getPageRequest(Map params){
        if(!isNotNullOrEmpty(params)){
            params = new HashMap();
        }
        Sort sort = Sort.by(Sort.Direction.fromString(params.getOrDefault("sortDir", AppUtils.DEFAULT_PAGE_SORT_DIR).toString()),
                params.getOrDefault("sortBy", AppUtils.DEFAULT_PAGE_SORT).toString());

        Integer pageNo = getParamToInteger(params, AppUtils.DEFAULT_PAGE_NUMBER, "page");
        Integer pageSize = getParamToInteger(params, AppUtils.DEFAULT_PAGE_SIZE, "size");

        PageRequest page = PageRequest.of(  pageNo - 1, pageSize, sort);

        return page;
    }

    /**
     * This  method is used to fetch an integer value from a Map with its default value
     * @param params The Map object
     * @param fieldName The name of the intended field
     * @param defaultVal The default value for the field to be extracted
     * @return Integer
     */
    public static Integer getParamToInteger(Map params, Integer defaultVal, String fieldName){
        Integer value = defaultVal;
        if(params != null && fieldName != null && params.get(fieldName) != null){
            try{
                var page2 = Integer.parseInt(params.get(fieldName).toString());
                if(page2 > 0){
                    value = page2;
                }
            }catch(Exception e){
                System.out.println("Invalid " + fieldName + " number");
            }
        }
        return value;
    }

    /**
     * This method is used to check if a string is not null or empty
     * @param str String value
     * @return Boolean
     * @author PrinceAh
     * @createdAt 15th July 2023
     * @modified
     * @modifiedBy
     * @modifiedAt
     */
    public static boolean isNotNullOrEmpty(Object str) {
        try{
            if (str == null) {
                return false;
            }
            if (str instanceof Collection<?>)
                return !((Collection<?>) str).isEmpty();

            if (str instanceof LinkedHashMap<?,?>)
                return !((LinkedHashMap<?, ?>) str).isEmpty();

            if (str instanceof Map<?, ?>)
                return !((Map<?, ?>) str).isEmpty();

            if (str instanceof Optional<?>)
                return ((Optional<?>) str).isPresent();

            if (str instanceof String)
                return !str.toString().trim().equalsIgnoreCase("");

            return Objects.nonNull(str);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    public static ZonedDateTime parseZoneDateTime(String date){
        if(date == null || date.equalsIgnoreCase("")){
            return null;
        }
        /**
         * Calculating if the Date was ZonedDateTime
         */
        try {
            ZonedDateTime parsedZone = ZonedDateTime.parse(date);
            return parsedZone;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        /**
         * Calculating if the Date was LocalDateTime
         */
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(date);
            return parsedDateTime.atZone(ZoneId.systemDefault());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        /**
         * Calculating if the Date was LocalDate
         */
        try {
            LocalDate parsedLocal = LocalDate.parse(date);
            return parsedLocal.atStartOfDay(ZoneId.systemDefault());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * This method is used to split and transform T, the object in question, string separated by commas into List<UUID>
     * @param data
     * @return  List<T>
     */
    public static <T> List<T> getListFromString(String data, Function<String, T> mapper) {
        try {
            if (data == null || data.trim().isEmpty()) {
                return new ArrayList<>();
            }
            String[] values = data.split(",");
            return Arrays.stream(values)
                    .map(mapper)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }


    /**
     * This is used to fetch a value from a Map
     * @param data  Map
     * @return Object from a map
     */
    public static Object getNameFromMap(Map data) {
        if(AppUtils.isNotNullOrEmpty(data)){
            return data.getOrDefault("name", null);
        }
        return null;
    }

    /**
     * This method is used to convert a camelcase String to snakeCase
     * @param value
     * @return String of snake format
     */
    public static String transformToSnake(String value){
        String str = "";
        var charList = value.toCharArray();
        for(int i=0; i < charList.length; i++){
            if(Character.isUpperCase(charList[i])){
                str += "_";
            }
            str += Character.toLowerCase(charList[i]);
        }
        return str;
    }

    /**
     * This method maps a page of a model to a Pagination object.
     *
     * @param page The Page object containing the IssueTypeModel entities.
     * @return A Pagination object containing the mapped entities, page information, and total element count.
     *         Returns null if the input Page is null.
     */
    public static <T> Pagination mapToPagination(Page<T> page) {
        if (page == null) {
            return null;
        }
        return new Pagination(page.getContent(), page.getPageable(), (int) page.getTotalElements());
    }

    /**
     * Retrieves the roles assigned to the currently authenticated user.
     * Returns an empty list if the user is not authenticated or an error occurs.
     *
     * @return A collection of GrantedAuthority objects representing the user's roles.
     */
    public static Collection<? extends GrantedAuthority> getUserRoles() {
        try {
            if (authentication().isAuthenticated()) {
                return authentication().getAuthorities();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the given roles collection contains the "ADMIN" role.
     *
     * @param roles A collection of roles assigned to a user.
     * @return True if the "ADMIN" role is present, false otherwise.
     */
    public static boolean hasAdminRole(Collection<? extends GrantedAuthority> roles) {
        return roles.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.contains("ADMIN"));
    }

    /**
     * Checks if the given roles collection contains the "AUTHORIZER" role.
     *
     * @param roles A collection of roles assigned to a user.
     * @return True if the "AUTHORIZER" role is present, false otherwise.
     */
    public static boolean hasAuthorizerRole(Collection<? extends  GrantedAuthority> roles) {
        return roles.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.contains("AUTHORIZER"));
    }

    /**
     * Checks if the given roles collection contains the "USER" role.
     *
     * @param roles A collection of roles assigned to a user.
     * @return True if the "USER" role is present, false otherwise.
     */
    public static boolean hasUserRole(Collection<? extends  GrantedAuthority> roles) {
        return roles.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.contains("USER"));
    }

    /**
     * Checks if the given roles collection contains any of the specified roles.
     *
     * @param roles A collection of roles assigned to a user.
     * @param str   A list of role names to check for.
     * @return True if any of the specified roles are present, false otherwise.
     */
    public static boolean hasRole(Collection<? extends GrantedAuthority> roles, List<String> str){
        return roles.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> str.stream().anyMatch(role::contains));
    }

    /**
     * Retrieves the UUID of the currently authenticated user.
     * Returns null if the user is not authenticated or an error occurs.
     *
     * @return The UUID of the authenticated user, or null if not authenticated.
     */
    public static UUID getAuthenticatedUserId() {
        try {
            if (authentication().isAuthenticated()){
                return UUID.fromString(authentication().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves username of the currently authenticated user.
     * Returns null if the user is not authenticated or an error occurs.
     *
     * @return The UUID of the authenticated user, or null if not authenticated.
     */
    public static String getAuthenticatedUserName(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(isNotNullOrEmpty(authentication) &&
                    authentication.isAuthenticated() &&
                    isNotNullOrEmpty(authentication.getPrincipal())){
                var claims = ((Jwt) authentication.getPrincipal()).getClaims();
                if (isNotNullOrEmpty(claims)) {
                    var firstName = claims.getOrDefault("name", null);
                    System.out.println(firstName);
                    if(isNotNullOrEmpty(firstName)){
                        return firstName.toString();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves the current authentication object from the SecurityContextHolder.
     *
     * @return The Authentication object representing the current user's authentication.
     */
    public static Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * This method is used to fetch the Client Role from a Jwt
     * @param principal
     * @return List of client roles
     */
    public static List<String> getClientRoles(Jwt principal, String clientName){
        if(!isNotNullOrEmpty(clientName)){
            clientName = "ceptra_op";
        }
        Object resourceAccess = principal.getClaims().get("resource_access");
        if(AppUtils.isNotNullOrEmpty(resourceAccess)){
            Object clientMap = ((Map<String, Object>) resourceAccess).get(clientName);
            return getRolesFromObject(clientMap);
        }
        return new ArrayList<>();
    }

    /**
     * This method is used to fetch the Client Roles from a Jwt
     * @return
     * @author PrinceAh
     * @createdAt 30th July 2024
     * @modified    Extracted a part to make a generic method to return roles from object
     * @modifiedBy  30th July 2024
     * @modifiedAt
     */
    public static List<String> getClientRoles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()) {
            /*var clientName = "ceptra_op";*/         /* TODO Change this when Urgently*/
            Object resourceAccess = ((Jwt) authentication.getPrincipal()).getClaims().get("resource_access");
            if (AppUtils.isNotNullOrEmpty(resourceAccess)) {
                Collection clientMap = ((Map<String, Object>) resourceAccess).values();
                return getRolesFromObject(clientMap);
            }
        }
        return new ArrayList<>();
    }

    /**
     * This method is used to fetch Roles from an Object by structuring it as a Map<String, Object>
     * @param data
     * @return List<String>
     * @author PrinceAh
     * @createdAt 30th July 2024
     * @modified
     * @modifiedBy
     * @modifiedAt
     */
    public static List<String> getRolesFromObject(Object data){
        List<String> res = new ArrayList<>();
        if(AppUtils.isNotNullOrEmpty(data)){
            try{
                if(data instanceof Collection<?>){
                    ((Collection) data).stream().forEach(i -> res.addAll(AppUtils.getRoles(i)));
                }else{
                    return getRoles(data);
                }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return res;
    }


    public static List<String> getRoles(Object data){
        try{
            Object roleList = ((Map<String, Object>) data).get("roles");
            if(AppUtils.isNotNullOrEmpty(roleList)){
                return  (List<String>) roleList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
