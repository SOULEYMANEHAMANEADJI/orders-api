package com.luqman.rest_api_orders.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponseModel {
    private Long id;
    private String nom;
    private String prenom;
    private String adresse;
}
