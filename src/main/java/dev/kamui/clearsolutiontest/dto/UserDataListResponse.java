package dev.kamui.clearsolutiontest.dto;

import dev.kamui.clearsolutiontest.model.User;

import java.util.List;

public record UserDataListResponse(int code, List<User> data) {
}
