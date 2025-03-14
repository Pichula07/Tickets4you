package br.com.joaomurari.event4you.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.joaomurari.event4you.dto.ZipCodeResponse;

@FeignClient(name = "api-cep", url = "https://viacep.com.br/ws")
public interface ZipCodeClient {

    @GetMapping("/{zipCode}/json")
    ZipCodeResponse getZipCodeInfo(@PathVariable("zipCode") String zipCode);
}