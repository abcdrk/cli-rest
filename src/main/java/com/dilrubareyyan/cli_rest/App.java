package com.dilrubareyyan.cli_rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class App implements CommandLineRunner {

	private String method;
	private String id;
	private String title;
	private String content;
	private boolean quit = false;

	public static void main(String args[]) {

		// disabled banner, don't want to see the spring logo
		SpringApplication app = new SpringApplication(App.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {

		Writer wrt = new OutputStreamWriter(System.out);
		Reader rdr = new InputStreamReader(System.in);
		BufferedReader brdr = new BufferedReader(rdr);
		wrt.write("*************** CLI-REST-APPLICATION ***************");

		while (!quit) {

			wrt.write("\n\nSelect Method: CREATE, UPDATE, GET, GET ALL, DELETE\n");
			wrt.write("Press Q to QUIT\n");
			wrt.flush();
			method = brdr.readLine();

			RestTemplate restTemplate = new RestTemplate();

			switch (method) {
				case "Q":
					quit = true;
					wrt.write("PROGRAM TERMINATED\n");
					wrt.flush();
					break;

				case "CREATE":
					//wrt.write("not implemented yet :)");
					wrt.write("Enter blog title:\n"); 
			        wrt.flush();
			        title = brdr.readLine();
			        wrt.write("Enter blog content:\n");
			        wrt.flush();
			        content = brdr.readLine();
			        Blog blog = new Blog(title,content);
			        // POST the blog object.
					blog = restTemplate.postForObject("http://localhost:8080/blog", blog, Blog.class);
			        wrt.write("\nCREATED: " + blog.toString());
			        wrt.flush();
					break;

				case "UPDATE":
					wrt.write("Enter blog ID to update:\n");
					wrt.flush();
					id = brdr.readLine();
					Blog check = null;
					try { 
	                     check = restTemplate.getForObject("http://localhost:8080/blog/" + id, Blog.class);
	                  // if no content it returns null
	                } catch (HttpServerErrorException e) {
	                	// SHOULD'VE CHECK SOME CONDITIONS BUT SKIPPED IT.
	                	// e.getCause() instanceof .....
	                    wrt.write("INVALID EXPRESSION\n");
	                    continue;
	                }
					if(check == null) {
						wrt.write("ID not found!\n");
						break;
					}
					wrt.write("Enter new blog title:\n"); 
			        wrt.flush();
			        title = brdr.readLine();
			        wrt.write("Enter new blog content:\n");
			        wrt.flush();
			        content = brdr.readLine();
			        Blog blog2 = new Blog(title,content);
					restTemplate.put("http://localhost:8080/blog/" + id, blog2);
					// if no content it returns null
					wrt.write("\n");
					// Just for a nice view. Not necessary.
					blog2.setId(Integer.parseInt(id));
					wrt.write(blog2 == null ? "ID not found!\n" : "UPDATED: "+ blog2.toString());
					wrt.flush();
					break;
					
				case "GET":
					wrt.write("Enter the blog ID to view:\n");
					wrt.flush();
					id = brdr.readLine();
					Blog post = null; 
					try { 
	                     post = restTemplate.getForObject("http://localhost:8080/blog/" + id, Blog.class);
	                  // if no content it returns null
	                } catch (HttpServerErrorException e) {
	                	// SHOULD'VE CHECK SOME CONDITIONS BUT SKIPPED IT.
	                	// e.getCause() instanceof .....
	                    wrt.write("INVALID EXPRESSION\n");
	                    continue;
	                }
					wrt.write(post == null ? "ID not found!\n" : post.toString());
					wrt.flush();
					break;
					
				case "GET ALL":
					String str = restTemplate.getForObject("http://localhost:8080/blog", String.class);
					// if no content it returns null
					wrt.write(str == null ? "NO POST TO VIEW" : str.toString());
					wrt.flush();
					break;

				case "DELETE":
					wrt.write("Enter the blog ID to delete:\n");
					wrt.flush();
					id = brdr.readLine();
					try { 
						restTemplate.delete("http://localhost:8080/blog/" + id);
	                } catch (HttpServerErrorException e) {
	                	// SHOULD'VE CHECK SOME CONDITIONS BUT SKIPPED IT.
	                	// e.getCause() instanceof .....
	               		wrt.write("BLOG NOT FOUND\n");
		                continue;
	                }
					wrt.write("DELETED POST "+ id + " SUCCESFULLY");
					wrt.flush();
					break;
				}
			}
		}
}
