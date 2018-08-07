package org.lhyf.cache.config;

import java.util.Date;

/****
 * @author YF
 * @date 2018-08-07 19:07
 * @desc User
 *
 **/
public class User {
    private Integer id;
    private String name;
    private String address;
    private Date birth;

    public User() {
    }

    public User(Integer id, String name, String address, Date birth) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.birth = birth;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }
}
