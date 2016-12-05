/*
 *  Copyright 2016 Son Nguyen <mail@gimu.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.gimu.discordnano.commands.anime;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.StringEscapeUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.util.HTTPUtil;
import org.gimu.discordnano.util.MALInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SubCommand(
        alias = {"anime"},
        description = "",
        usage = "anime (<query> | view [index])"
)
public class AnimeCommand extends AbstractCommand {

	private static long lastExecution = 0L;
	private static final String MAL_USER = DiscordNano.config.getString("mal_user");
	private static final String MAL_PASS = DiscordNano.config.getString("mal_pass");

	private static HashMap<Integer, MALInfo> animeMap = new HashMap<>();

    public AnimeCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		Message response;

		// Viewing
		String index = args.length >= 2 ? args[1] : "";
		if (args[0].toLowerCase().equals("view")) {
			if (index.length() == 0) {
                response = viewRecent(author);
			} else {
                response = viewEntry(author, index);
			}
		} else {
            // Searching
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length - 1; i++) {
                sb.append(args[i] + " ");
            }
            sb.append(args[args.length - 1]);
            response = searchMAL(author, sb.toString());
        }
        return Optional.of(response);
	}

	private Message viewRecent(User author) {
        if (animeMap.isEmpty()) {
			return MessageUtil.buildFramedMessage(author, "There are no recent queries to show!", true);
		}
        StringBuilder content = new StringBuilder();
        int i = 0;
        for (Map.Entry<Integer, MALInfo> entry : animeMap.entrySet()) {
            content.append("**[" + i + "]** " + entry.getValue().title);
            if (entry.getValue().english.length() != 0) content.append(" / " + entry.getValue().english);
            content.append("\n");
            i++;
        }
        return MessageUtil.buildFramedMessage(author, content.toString(), true);
	}

	private Message viewEntry(User author, String index) {
		MALInfo entry = animeMap.get(Integer.parseInt(index));

        if (!index.matches("^\\d+$") || entry == null) {
			return MessageUtil.buildFramedMessage(author, "Not a valid index or no recent queries saved!", true);
		}
        return MessageUtil.buildFramedMessage(author, entry.title
                + (entry.english.length() != 0 ? "\n" + entry.english : "")
                + "**\n**Type:** " + entry.type
                + " **| Episodes:** " + entry.episodes
                + " **| Status:** " + entry.status
                + " **| Score:** " + entry.score
                + "**\n\n" + entry.synopsis
                + "\n\n**<http://www.myanimelist.net/anime/" + entry.id + ">**", true);
	}

	private Message searchMAL(User author, String query) {
		// MALCommand/Anime search
		if (MAL_USER == "" || MAL_PASS == "") {
            return MessageUtil.buildFramedMessage(author, "MAL login not configured.", true);
		} else if (lastExecution != 0) {
            long currentExecution = System.currentTimeMillis();
            long time = (currentExecution - lastExecution) / 1000;
            if (time < 5) {
                return MessageUtil.buildFramedMessage(author, "Please wait 5 seconds before submitting another query.", true);
            }
            lastExecution = currentExecution;
        }

        StringBuilder content = new StringBuilder();
        try {
            lastExecution = System.currentTimeMillis();
            String parameters = "q=" + URLEncoder.encode(query, "UTF-8");
            InputStream stream = HTTPUtil.sendAuthGet("https://myanimelist.net/api/anime/search.xml", parameters, MAL_USER, MAL_PASS);

            String id = "", title = "", english = "", episodes = "", score = "", type = "", status = "", synopsis = "";

            // XML parsing
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("entry");

            content.append("MAL Anime query result\n\n");

            // Reset recent map
            if (nodes.getLength() > 0) animeMap.clear();

            int limit = (nodes.getLength() <= 21) ? nodes.getLength() : 21;

            for (int i = 0; i < limit; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    id = element.getElementsByTagName("id").item(0).getTextContent();
                    title = element.getElementsByTagName("title").item(0).getTextContent();
                    english = element.getElementsByTagName("english").item(0).getTextContent();
                    episodes = element.getElementsByTagName("episodes").item(0).getTextContent();
                    score = element.getElementsByTagName("score").item(0).getTextContent();
                    type = element.getElementsByTagName("type").item(0).getTextContent();
                    status = element.getElementsByTagName("status").item(0).getTextContent();
                    synopsis = element.getElementsByTagName("synopsis").item(0).getTextContent();

                    content.append("**[" + i + "]** " + title);
                    if (english.length() != 0) content.append(" / " + english);

                    content.append("\n");
                    if (synopsis.length() > 0) {
                        if (synopsis.length() > 500) {
                            synopsis = synopsis.substring(0, 500) + "...";
                        }
                        synopsis = StringEscapeUtils.unescapeHtml(synopsis);
                        synopsis = synopsis.replaceAll("<br[^>]*>", " ").replaceAll("\\[/?i\\]", "*").replaceAll("\\[/?b\\]", "**").replaceAll("\\[([^\\]]+)\\]", "");
                    }

                    animeMap.put(i, new MALInfo(id, title, english, episodes, score, type, status, synopsis));
                }
            }

            content.append("\nList temporarily saved. Write `" + DiscordNano.PREFIX + "anime anime view <index>` to examine an entry.");

        } catch (Exception e) {
            content.setLength(0);
            content.append("I couldn't find an entry fitting that phrase.");
            NanoLogger.error(e.getMessage());
        }

        return MessageUtil.buildFramedMessage(author, content.toString(), true);
	}
}