package org.example.identityservice.controllers.admin.pages;

import lombok.RequiredArgsConstructor;
import org.example.identityservice.models.entity.Client;
import org.example.identityservice.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPagesController {
    private final ClientService clientService;

    @GetMapping("/home")
    public String home() {
        return "admin/home"; // Returns the view id for the admin home page
    }

    @GetMapping("/clients")
    public String clients(Model model) {
        List<String> clientsNameAndId = clientService.findAllClientIds();
        model.addAttribute("clientIds", clientsNameAndId);
        return "admin/clients/clients-list";
    }

    @GetMapping("/clients/{id}")
    public String clientDetails(@PathVariable Integer id, Model model) {
        Client client = clientService.findById(id);
        if (client == null) {
            return "redirect:/errors/404";
        }
        model.addAttribute("client", client);
        return "admin/client-details"; // Returns the view id for the client details page
    }

    @GetMapping("/clients/create")
    public String showCreateClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "admin/client-create";
    }

}
