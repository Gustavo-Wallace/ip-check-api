package br.com.gustavo.ip_check_api.utils;

import java.util.ArrayList;
import java.util.List;

import br.com.gustavo.ip_check_api.dtos.IpAddressImportItemDTO;

public class IpAddressCsvParser {

    private static final String DEFAULT_DESCRIPTION = "Imported from CSV text";

    private IpAddressCsvParser() {
    }

    public static List<IpAddressImportItemDTO> parse(String csvContent) {
        List<IpAddressImportItemDTO> items = new ArrayList<>();

        if (csvContent == null || csvContent.isBlank()) {
            return items;
        }

        String[] lines = csvContent.split("\\R");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.isBlank()) {
                continue;
            }

            if (i == 0 && line.toLowerCase().contains("address")) {
                continue;
            }

            String[] columns = line.split(",", -1);

            if (columns.length == 0) {
                continue;
            }

            String address = columns[0].trim();

            if (address.isBlank()) {
                continue;
            }

            String description = DEFAULT_DESCRIPTION;

            if (columns.length > 1 && !columns[1].trim().isBlank()) {
                description = columns[1].trim();
            }

            items.add(IpAddressImportItemDTO.builder()
                    .address(address)
                    .description(description)
                    .build());
        }

        return items;
    }
}