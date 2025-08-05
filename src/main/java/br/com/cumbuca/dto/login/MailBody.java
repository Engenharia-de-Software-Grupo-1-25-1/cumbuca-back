package br.com.cumbuca.dto.login;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}