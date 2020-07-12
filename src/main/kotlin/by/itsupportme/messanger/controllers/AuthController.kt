package by.itsupportme.messanger.controllers

import by.itsupportme.messanger.dto.UserDto
import by.itsupportme.messanger.security.jwt.JwtTokenProvider
import by.itsupportme.messanger.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        @Autowired
        val userService: UserService,

        @Autowired
        val jwtTokenProvider: JwtTokenProvider,

        @Autowired
        val authenticationManager: AuthenticationManager
) {
        @PostMapping("/register")
        fun register(@RequestBody userDto: UserDto) {
                userService.register(userDto.toUser())
        }

        @PostMapping("/login")
        fun login(@RequestBody userDto: UserDto) : ResponseEntity<Any> {
                try{
                        val username = userDto.username
                        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username,userDto.password))
                        val user = userService.findByUsername(username) ?: throw UsernameNotFoundException("user name not found")
                        val token = jwtTokenProvider.createToken(username,user.roles)
                        val response = HashMap<Any,Any>()
                        response["token"] = "Bearer $token"
                        response["username"] = username
                        return ResponseEntity.ok(response)

                } catch (e: AuthenticationException){
                        throw BadCredentialsException("Invalid username or password")
                }
        }
}