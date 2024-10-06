package io.github.playlistmanager.controller;

import io.github.playlistmanager.dto.ChzzkChannelConnectDto;
import io.github.playlistmanager.dto.JoinMemberDto;
import io.github.playlistmanager.dto.PlaylistDto;
import io.github.playlistmanager.service.impl.MusicServiceImpl;
import io.github.playlistmanager.service.impl.UserServiceImpl;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    private final MusicServiceImpl musicService;
    private final UserServiceImpl userService;

    public ViewController(MusicServiceImpl musicService, UserServiceImpl userService) {
        this.musicService = musicService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/")
    public String mainPage(Model model, SecurityContext securityContext) {
        String username = (String) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("playlistData", musicService.findAll(username));
        return "main";
    }

    @Secured("ROLE_USER")
    @GetMapping("/add")
    public String addPage() {
        return "playlist-create";
    }

    @Secured("ROLE_USER")
    @GetMapping("/update")
    public String updatePage(@RequestParam String playlistId, @RequestParam String playlistName, SecurityContext securityContext, Model model) {
        String username = (String) securityContext.getAuthentication().getPrincipal();
        PlaylistDto dto = musicService.findByIdAndPlaylistName(playlistId, playlistName, username);

        model.addAttribute("playlistName", dto.getPlaylistName());
        model.addAttribute("chzzkChannelId", dto.getChzzkChannelId());
        model.addAttribute("playlistId", playlistId);

        return "update";
    }

    @PostMapping("/signup")
    public String signUp(JoinMemberDto dto) {
        userService.joinUser(dto);
        return "login";
    }

    @Secured("ROLE_USER")
    @GetMapping("/playlist")
    public String view(@RequestParam String id, @RequestParam String name, Model model, SecurityContext securityContext) {
        String username = (String) securityContext.getAuthentication().getPrincipal();
        ChzzkChannelConnectDto connectDto = musicService.chzzkChannelConnect(musicService.findByIdAndPlaylistName(id, name, username));

        model.addAttribute("playlistName", name);
        model.addAttribute("playlistId", connectDto.getPlaylistId());
        model.addAttribute("chatChannelId", connectDto.getChatChannelId());
        model.addAttribute("accessToken", connectDto.getAccessToken());
        model.addAttribute("serverId", connectDto.getServerId());

        return "music";
    }

}
