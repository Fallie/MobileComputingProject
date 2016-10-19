package com.example.wyyz.snapchat.db;

/**
 * Created by leify on 2016/10/13.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.wyyz.snapchat.R;
import com.example.wyyz.snapchat.model.DiscoveryChannel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by leify on 2016/10/12.
 */

public class DataBaseOperator {

    private Context mContext;


    public DataBaseOperator(Context mContext)
    {
        this.mContext = mContext;
    }

    public void initialise()
    {
        String html1 = "<h1>Caltex confirms offer to acquire Woolworths Petrol business</h1>"
                + "<p>Caltex Australia has confirmed it has made a conditional and confidential offer to acquire Woolworths’ " +
                "fuel businesses and to continue the alliance between the two companies.</p>"
                + "<img src='https://www.macquariecentre.com.au/MacquarieCentre/media/contents/04_Stores/01_Store_Logos/Logo_Woolworths.png?ext=.png' />"
                + "<p>Woolworths has spent the past few months deciding whether or not to sell its nearly 600 Australian petrol " +
                "businesses, receiving incomplete and conditional proposals from a number of different unnamed parties.</p>"
                + "<p>Caltex is currently the exclusive supplier of petrol and diesel to Woolworths with annual sales volumes of " +
                "approximately 3.5 billion litres per annum.</p>"
                + "<img src='http://www.caltexforinvestors.com/imgs/home-banner-see.png' />"
                + "<p>The petrol giant said any possible transaction with Woolworths remains uncertain and is expected to take time " +
                "to complete, but that it will update the Australian Securities Exchange (ASX) if there are any changes with its wholesale " +
                "supply arrangements with Woolworths.</p>"
                + "<p>If the deal eventuates, Caltex would own almost 40 per cent of the retail fuel market in Australia. It currently has " +
                "an approximated 16 per cent stake.</p>";

        String html2 = "<h1>Police to target theft at Coles self-serve checkouts</h1>"
                + "<p>Police officers may not be your typical supermarket worker, but they have stepped in to try to tackle theft from self-serve checkouts at Coles.</p>"
                + "<img src='http://www.ausfoodnews.com.au/wp-content/uploads/onions-316x216.jpg' />"
                + "<p>Exploitation of the machines is not a novel phenomenon. Coles has reported that approximately one third of shoplifting from its NSW " +
                "and ACT stores was done using self-serve checkouts.</p>"
                + "<p>Stealing through self-serve checkouts includes either by not scanning an item on the machine or by scanning the item as a cheaper alternative, " +
                "so a customer is charged less.</p>"
                + "<p>There are many different reasons why customers may do this. The customer may feel frustrated at the implementation of self-serve technology, " +
                "or there could be a legitimate error on the part of the user or the machine.</p>"
                + "<p>Consumers may see the transaction as a ‘Robin Hood’ scenario where the loss would have minimal impact on the large supermarket. Consumers dealing " +
                "with a machine only may also feel less guilt than if they were forced to have a face-to-face interaction with a staff member.</p>";

        String html3 = "<h1>FACEBOOK is testing its latest addition in Australia before it’s released in the United States, but users are likely to find it exceedingly familiar.</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/ddc240dca22740130afa1f6fdf97c71e' />"
                + "<p>Facebook Messenger's new addition, called Messenger Day, lets users add text, stickers, and scribbles to photos, and pin them to their profile for 24 hours.</p>"
                + "<p>But anyone who has used Snapchat, where users can add text, stickers, and scribbles to photos and pin them to their profile for 24 hours, is likely to notice a strong resemblance.</p>"
                + "<p>Facebook product management director Peter Martinazzi said the social network had been working on versions of Messenger Day for the past two years, and the new additions were designed to " +
                "\"upgrade conversations\" by letting your friends and contacts know what you were doing that day.</p>"
                + "<p>There are many different reasons why customers may do this. The customer may feel frustrated at the implementation of self-serve technology, " +
                "or there could be a legitimate error on the part of the user or the machine.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/7c1d5d34f49227412808af2bd50ab515' />"
                + "<p>Consumers may see the transaction as a ‘Robin Hood’ scenario where the loss would have minimal impact on the large supermarket. Consumers dealing " +
                "with a machine only may also feel less guilt than if they were forced to have a face-to-face interaction with a staff member.</p>";

        String html4 = "<h1>ExoMars landing: European Space Agency’s experimental Schiaparelli probe enters orbit around Mars</h1>"
                + "<p>THE European Space Agency’s experimental Schiaparelli probe has entered the atmosphere of Mars, with scientists awaiting confirmation that the craft touched down safely.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/7d5e2ac5567f8aa22884714fe45e048e' />"
                + "<p>Its mother ship, which will analyse the atmosphere, went into orbit around the red planet.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/c334f8e68813f5d99666fb9dc01b7fe5' />"
                + "<p>Scientists said the gentle approach would turn into a six-minute hell ride when the probe plunged into the hot, dusty Martian atmosphere and hurtled toward the surface at 21,000 " +
                "kilometres an hour.</p>"
                + "<p>If all went to plan, Schiaparelli would deploy a parachute and then thrusters to slow down to 10km/h before hitting the surface.</p>"
                + "<p>Don McCoy, the manager of the ExoMars project of which the two craft are part, said some data had been received from the lander confirming its entry and the deployment of its parachute. " +
                "More information was expected later on Wednesday.</p>";

        String html5 = "<h1>Melbourne United wary of Brisbane Bullets’ scoring arsenal</h1>"
                + "<p>THEY are two of the best scoring teams in the NBL this year, yet neither Melbourne United nor the Brisbane Bullets boast one of the top five scorers.</p>"
                + "<p>Depth of contributors is the great strength of both and talk out of the United camp ahead of their showdown at Hisene Arena was all about keeping up a “tough cover” " +
                "on as many Bullets as possible.</p>"
                + "<p>Beal, as a Perth Wildcat last season, scored 40 points against Melbourne and with a season-high 21 in his third outing for the Bullets last week is beginning to find his range.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/98649631250148d5a0113e0ea96397a8' />"
                + "<p>But the Bullets also boast Olympian Cameron Bairstow and former United big man Daniel Kickert, a “sniper from the outside” according to Melbourne’s own hot-hand Todd Blanchfield." +
                "That’s four threats before even mentioning import Torrey Craig, who has lead the Bullets in scoring in two of their three games.</p>";

        String html6 = "<h1>Where is George Town? Richmond trade revisits Matthew Richardson geography fail</h1>"
                + "<p>RICHMOND great Matthew Richardson has become a surprise victim of the AFL trade period.\n" +
                "Richo will be forced to confront his geography demons after his beloved Tigers completed a trade deal to bring Sydney ruckman Toby Nankervis to Punt Rd.</p>"
                + "<p>Last year Richardson, a proud Tasmanian, dug himself into a giant hole when he got his Tasmanian geography hilariously wrong during a Channel 7 AFL broadcast.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/b10cd5404034d6346a4a8fd6971b57d9' />"
                + "<p>Richo proudly announced the town of George Town was inland, prompting a flood of calls to the station and social media posts pointing out it is in fact at the mouth of the Tamar River on the state’s north coast.</p>"
                + "<p>Why was Richardson tripped up by Google Maps? He was offering special comments about a Sydney player making his debut in that game — Toby Nankervis.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/fc0b67f8b42d156d9657fab4944cdf4a' />"
                + "<p>The Tigers champ might have hoped today’s news would slip through to the keeper, but it was quickly pointed out — by the club itself on Twitter.\n" +
                "Hopefully Richo has been studying an atlas in the past 12 months.</p>";

        String html7 = "<h1>Man touted as the United Kingdom Independence Party’s next leader says UKIP is now ‘ungovernable’</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/cb91917af5a2d7bc8d375d857bbab1e8' />"
                + "<p>AS the UK tumbles towards what is increasingly looking like a messy exit from the European Union, the political party that did the most to make Brexit a reality should be riding high.</p>"
                + "<p>Nothing could be further from the truth for the United Kingdom Independence Party, better known as UKIP.\n" +
                "As British Prime Minister Theresa May weighs up the merits of a “hard” or “soft” Brexit, the ranks of UKIP are being rocked by leadership squabbles, " +
                "resignations and even an alleged fist fight which left one politician splayed on the floor.</p>"
                + "<p>Now the latest person to jump ship from UKIP has said the party was “ungovernable” and could now be in a “death spiral”.</p>"
                + "<p>It has led some to question whether, with Brexit in train, UKIP’s reason to exist has vanished.</p>"
                + "<p>It was all so different on June 24, the day Britain woke to the news 52 per cent of its citizens had voted to leave the EU.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/a40a054fc194fb581a1614d2b50ff18b' />";

        String html8 = "<h1>Daigou sellers fear being pushed out of the market by big Australian brands</h1>"
                + "<p>THEY’VE made their fortunes reselling Aussie products to keen customers in China, but now that brands have cottoned on to the success of the daigou — the online traders making millions by recommending and " +
                "shipping their products to China — companies are keen to push them out of the process.</p>"
                + "<p>With Australian manufacturers expanding their operations to sell directly to Chinese people, private sellers are fearing the future of the multi-million dollar daigou trade may not be so profitable after all.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/08908bf71dccce923f7fc3c693faaebd' />"
                + "<p>International grey market traders, now better known by their Chinese name daigous, first gained public attention in Australia at the peak of last year’s baby formula shortage scandal.\n" +
                "Demand for Australian-made organic baby formula spiked in China, where trust in local dairy products had plummeted. The demand travelled all the way back to local Australian chemists and supermarkets where shelves were stripped by" +
                " Chinese shoppers bulk-buying the product and shipping it back home.</p>"
                + "<p>While some kept to sending the tins of the “white gold” to their friends and family as favours, others saw the profit potential and scaled up their businesses.\n" +
                "Daigous were selling tins of baby formula for up to $200 each to desperate buyers, and pocketing the hefty profits.</p>"
                + "<p>Since daigous began to make headlines, it’s become public knowledge their sales aren’t restricted to infant products. They’re also making big bucks buying, reselling and shipping all sorts of Australian-made products from fresh food, " +
                "to health and nutrition products, vitamins, and cosmetics.</p>";

        String html9 = "<h1>Scarlett Johansson opens Paris popcorn shop</h1>"
                + "<p>IT isn’t — until you think about it — an obvious career move for a movie star. Hollywood actress Scarlett Johansson is opening a popcorn shop.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/f442cdb019b288332843f8dc60db0a0d' />"
                + "<p>Not just any old popcorn joint but a gourmet one in Paris, the gastronomic capital of the world, where she spends much of her time.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/eed063abf461d103dafa276c6901a7ec' />"
                + "<p>The 31-year-old singer and model is even going to serve customers with her own fair hand.</p>"
                + "<p>The star of Lost in Translation will be serving up truffle, parmesan and sage flavoured popcorn — her personal favourite — to punters when her Yummy Pop shop opens in the city’s Marais district on Saturday.</p>"
                + "<p>The concept is a labour of love with her French husband, advertising executive Romain Dauriac. The couple hope that if their “Real Vermont Cheddar” and other savoury and sweet recipes are a hit, they will" +
                " open other shops elsewhere.</p>";

        String html10 = "<h1>James Weir recaps The Bachelorette Australia 2016 episode 9</h1>"
                + "<p>IT’S always a big moment when the super hot person you like eventually does something that isn’t perfect. And on Wednesday night’s episode of The Bachelorette, Lee showed his one big flaw.</p>"
                + "<p>To be upfront, Lee’s a beautiful sea prince. If he let me, I’d quit my job, move into his apartment and spend my days washing his underwear and cooking him dinner and calling his mum and telling her he’s OK.\n" +
                "But Lee has shown a side to himself that’s too difficult to watch.</p>"
                + "<p>Before most things come crashing down, they begin on a high. And that’s the same for Lee who scores the single date.\n" +
                "When he meets up with Georgia on an overcast weekday by some river, she tells him about her love of romance and old movies and how she wanted to create a date that really symbolises this. Apparently this love can be summed up by that run-jump-and-lift move from Dirty Dancing, " +
                "and they spend hours trying to recreate it.</p>"
                + "<p>A stuntman lumbers on to the set to give them a talking-to about health and safety precautions. I’d like to point out I’ve successfully recreated this move several times while drunk in backyards — and some sparsely decorated living rooms — " +
                "and never once had the supervision of a stuntman.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/130ac5663cd3b408b11f016bbcec3bfe' />"
                + "<p>They try to use the dance move as a clunky metaphor for relationships — something about trust and communication and not intentionally dropping other people’s bodies.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/8419ee8218455e2c97bd50c482755141' />";

        String html11 = "<h1>Video cams to take closer look at man-made objects in MH370 search</h1>"
                + "<p>VIDEO cameras will be sent to the bottom of the Southern Indian Ocean to have a closer look at “man-made” objects detected by ships searching for MH370.</p>"
                + "<p>With the weather hopefully improving in coming weeks, the two vessels left in the search are being fitted with more sensitive equipment for the final stages of the operation.</p>"
                + "<p>In the case of the Chinese vessel, Dong Hai Jiu 101, a remotely operated vehicle (ROV) is being remobilised on the ship, equipped with a range of instruments including video cameras.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/82bcf89e9369b2c52d7b23cc8520d3c3' />"
                + "<p>It will then be the ship’s job to revisit sites where previous sonar contacts have detected objects that “exhibit man-made properties”.\n" +
                "“None of the sonar contacts targeted for reacquisition exhibit the characteristics of a typical aircraft debris field,” said an Australian Transport Safety Bureau update.</p>"
                + "<p>“However some exhibit man-made properties and therefore must be investigated further to be positively eliminated.”\n" +
                "The update went on to say that poor weather had prevented the safe deployment of the ROV but now sea conditions were improving.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/21cfc64ca06b23e4bf56df79c3dad823' />";

        String html12 = "<h1>How to date in 31 different countries</h1>"
                + "<p>A FIRST date probably involves a steamy one night stand in Iceland, sneaking into your beloved’s bedroom under the cover of darkness in Bhutan, or a chaperone in Iran.</p>"
                + "<p>Welcome to the confusing world of international dating. Aside from obvious difficulties like linguistic and cultural differences, dating traditions vary widely from country to country.</p>"
                + "<p>British gambling website Slots Info has compiled an infographic featuring dating traditions from around the world, and the results are surprising.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/cd0c810302ac9e0f048794864ff75673' />"
                + "<img src='http://cdn.newsapi.com.au/image/v1/1e7cf59597068bf912feb7c4a41727b8' />"
                + "<img src='http://cdn.newsapi.com.au/image/v1/192628aef9b35a57d78b461fb6e9847f' />"
                + "<img src='http://cdn.newsapi.com.au/image/v1/829b917f0a809f77a6458a386ed9d6c2' />";

        String html13 = "<h1>We need to stop forcing kids to kiss adults</h1>"
                + "<p>FIRST, Donald Trump bragged that he kisses women without their consent (“It’s like a magnet. Just kiss. I don’t even wait!” he boasted in his infamous bus video).</p>"
                + "<p>Then a handful of women — OK, two handfuls if we’re talking about Trump’s hands — gave us vivid verbal accounts of the real estate mogul assaulting them in exactly the way he described.</p>"
                + "<p>And now we’ve been forced to see the angry Pomeranian actually perform one of his unwanted smackers in the flesh, when he engulfed a small girl at his recent rally in Green Bay, Wisconsin, and lurched at her," +
                " lips puckered. Twice.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/8e4aaeeff81d48335a5f6f61271ecf7a' />"
                + "<p>“Wow, so beautiful,” he leered. “Bring her, she’s so beautiful.”\n" +
                "The poor girl squirms uncomfortably, and does her best to avoid Trump’s suffocating embrace, even as he attempts to kiss her directly on the lips.</p>"
                + "<p>Surely the time has passed when any politician — even the ones who are demonstrably less lecherous than Donald Trump — should be kissing strangers’ children on the campaign trail.\n" +
                "This kid was anything but pleased at being forced to snuggle with Tony Abbott</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/176fc7989ba38038fdee84f705eeaceb' />";

        String html14 = "<h1>Weight Watchers cops backlash after their PR stunt angers women around the country</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/8ddd365336e2c38d527746ec52f51f86' />"
                + "<p>ACCORDING to new research launched by Weight Watchers, more than half of Australian women only have sex in the dark — because they don’t want their partner to see them naked.</p>"
                + "<p>But that’s not what has left women around the country with a sour taste in their mouth.</p>"
                + "<p>Basically, the research and PR campaign targeted vulnerable women, by suggesting those who feel uncomfortable getting naked with the lights on should go on a diet — and they did that by sending women journalists a" +
                " light bulb, and a pretty poorly composed note.</p>"
                + "<p>The NYSE-listed brand, which has operates in about 30 countries globally, decided to send female journalists “mood light bulbs” to support their campaign, suggesting women don’t like to have sex with the light on" +
                " because of body image concerns.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/d399ee2d335cb58db0ad80773da40b04' />"
                + "<p>Along with the light bulb, journalists also received a note that read “Let’s be honest for a minute, sex is pretty damn fantastic. But if you’ve ever felt self-conscious in the sack you’re not alone — we’ve heard " +
                "that more than half of women have avoided sex because they were worried about how they look.</p>";

        String html15 = "<h1>US general claims Islamic State leaders are abandoning Mosul</h1>"
                + "<p>ISLAMIC State leaders have been fleeing Mosul as US-backed local forces close in on the jihadists’ last Iraqi stronghold, a US general said.</p>"
                + "<p>“We are telling Daesh (IS) that their leaders are abandoning them. We’ve seen a movement out of Mosul,” US Army Major General Gary Volesky said in a" +
                " video briefing from Baghdad, as a wide-scale operation to retake the militant-held city entered its third day.</p>"
                + "<p>Reuters reported IS leader Abu Bakr al-Baghdadi and explosives expert Fawzi Ali Nouimeh are both believed to be holed up in Mosul.</p>"
                + "<p>Volesky predicted that foreign fighters will end up forming a large contingent of jihadists remaining in the city, as they have nowhere else to go. Lt. Gen. Talib Shaghati," +
                " a senior Iraqi general, said about 6000 IS fighters are inside the city, but did not say how many of them are foreigners.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/ba7fe5a1b40fe60c35648fe35d4c8552' />"
                + "<img src='http://cdn.newsapi.com.au/image/v1/2b6406eb535cc556f207d71727aa74f1' />";

        String html16 = "<h1>Nigeria vows to rescue remaining girls</h1>"
                + "<p>NIGERIAN President Muhammadu Buhari has met the 21 Chibok girls who were released by Boko Haram last week, pledging to “redouble” his efforts to rescue those still being held.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/37235bce7ab6e1080165a1d49d495f72' />"
                + "<p>Speaking at the presidential villa in Nigeria’s capital of Abuja, Mr Buhari addressed the girls and their families saying “we shall redouble efforts to ensure that we fulfil our pledge of bringing the remaining girls back home”.</p>"
                + "<p>Mr Buhari said that he hoped the girls would go on to complete their education after their ordeal at the hands of Boko Haram, whose name in the Hausa language spoken across northern" +
                " Nigeria means “Western education is sin”.</p>"
                + "<p>“Obviously it is not too late for the girls to go back to school and continue the pursuit of their studies,” Mr Buhari said to the girls, who were clad in new, brightly" +
                " coloured dresses and head wraps.</p>"
                + "<p>“These dear daughters of ours have seen the worst that the world has to offer. It is now time for them to experience the best that the world can do for them.”</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/1dcc3c0b04bd963e0734de5bf26dc9d9' />";

        String html17 = "<h1>RUSH HOUR: The stories you need to know today</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/735292b8aeda96b393f4df8bbecf55fc' />"
                + "<p>Trump’s secret weapon for final debate</p>"
                + "<p>Donald Trump will seek to give his bid for the White House a much-needed boost today as he clashes with Hillary Clinton for the final debate before America goes to the polls.</p>"
                + "<p>Launching a last ditch offensive, Mr Trump will bring Barack Obama’s half brother — a supporter of the billionaire — to the showdown as his guest and will rally his supporters to look out for" +
                " signs the election is being rigged.</p>"
                + "<p>Mr Trump trails his rival by an average of almost 7 per cent in national polls, and is struggling just as much in crucial swing states where the election will be decided.</p>"
                + "<p>Get ready.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/6478d9cea1623c69ecccd53ca1f8a2a2' />";

        String html18 = "<h1>Malmsbury Youth Justice Centre will be reviewed following a riot last month</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/b6b7e355a6af55e92a85da4cac86a16f' />"
                + "<p>INSIDE the strong walls of one of Australia’s most notorious detention centres are some of the worst young crims in the country.</p>"
                + "<p>Malmsbury Youth Justice Centre is a place of discipline, but at times the youths have gained control, tearing through the centre on violent rampages, threatening staff and even making them fear for their lives.</p>"
                + "<p>Last month some inmates triggered two hours of terror when they scaled the detention centre roof with makeshift prison knives and metal poles.\n" +
                "Ripping off their shirts, they violently bashed in windows and damaged the building while making threats to staff.</p>"
                + "<p>Also last month, a WorkSafe report revealed staff at the detention centre were at risk of being killed and Malmsbury Youth Justice Centre was said to be worse than an adult prison.\n" +
                "The Herald Sun reported women who worked at the detention centre copped abuse and thugs would threaten to rape them.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/e903afeb0b12c98623c2da1e0f5da1d8' />";

        String html19 = "<h1>RSPCA wants power to issue fines</h1>"
                + "<p>THE RSPCA is seeking the power to issue infringement notices that would mean your dog’s fleas could earn you an on-the-spot fine.</p>"
                + "<p>The animal welfare group has begun talks with the state government requesting authority to issue fines over low level animal cruelty offences following an independent review of the RSPCA Victoria.</p>"
                + "<p>The recommendation was included in the review headed by former police commissioner Neil Comrie.</p>"
                + "<p>RSPCA Victoria CEO Liz Walker told news.com.au the group “strongly supports” the idea.</p>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/f6bd9f12752ff49182f4a811369c679c' />"
                + "<p>“At the moment, there isn’t much else that our inspectors can do other than prosecution. So if a person is not tethering their animal properly, for example, and we can issue an on-the-spot fine it would" +
                " really send a strong message,” she said in a statement.</p>";

        String html20 = "<h1>Aussie pets are undergoing human like health procedures as owners strive to keep them healthy</h1>"
                + "<img src='http://cdn.newsapi.com.au/image/v1/1010a50f0dc7b84fa2dc8f3484813136' />"
                + "<p>MILLIONS of Australia’s pampered pooches and fluffy felines are undergoing human like health procedures as their owners strive to keep them happy and healthy.</p>"
                + "<p>According to PetSure, Australia’s leading pet insurance specialist, our pets are being increasingly humanised, with more owners treating their pets like a member of the family.</p>"
                + "<p>As we stop to appreciate the health and happiness of our pets this World Animal Day, Dr Magdoline Awad, PetSure’s Chief Veterinary Officer, and former Chief Veterinarian of the RSPCA NSW, weighs in on our evolving approach to pet health and wellbeing.</p>"
                + "<p>“With 63 per cent of Australian households owning a pet, it’s no doubt we love our animals. But more than ever, Australia’s pets are becoming an integral part of the family unit, and the health procedures being performed on our pets certainly prove that,” " +
                "Dr Awad told News Corp Australia.</p>"
                + "<p>She said pet owners are increasingly adopting the latest technologies and procedures for their pets.</p>"
                + "<img src=http://cdn.newsapi.com.au/image/v1/cd7009df914bfa80788179f15977e74a' />";

        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Channel", null, null, null, null, null, null);
//        Log.e("setting initialise","saved "+cursor.getCount());
        if(cursor.getCount()==0) {
            for(int i=0; i<10; i++) {
                ContentValues values = new ContentValues();
                values.put("channelId", i);
                if(i==0)
                    values.put("name", "Discovery Channel: Food");
                else if(i==1)
                    values.put("name", "Discovery Channel: Technology");
                else if(i==2)
                    values.put("name", "Discovery Channel: Sport");
                else if(i==3)
                    values.put("name", "Discovery Channel: Finance");
                else if(i==4)
                    values.put("name", "Discovery Channel: Entertainment");
                else if(i==5)
                    values.put("name", "Discovery Channel: Travel");
                else if(i==6)
                    values.put("name", "Discovery Channel: Lifestyle");
                else if(i==7)
                    values.put("name", "Discovery Channel: World");
                else if(i==8)
                    values.put("name", "Discovery Channel: National");
                else
                    values.put("name", "Discovery Channel: Pets");
                values.put("visitNum", 0);
                values.put("subscriptionState", 0);

                Resources res = mContext.getResources();
                Bitmap bmp;
                if(i==0)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.food_channel);
                else if(i==1)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.technology_channel);
                else if(i==2)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.sport_channel);
                else if(i==3)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.finance_channel);
                else if(i==4)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.entertainment_channel);
                else if(i==5)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.travel_channel);
                else if(i==6)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.lifestyle_channel);
                else if(i==7)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.world_channel);
                else if(i==8)
                    bmp = BitmapFactory.decodeResource(res, R.drawable.national_channel);
                else
                    bmp = BitmapFactory.decodeResource(res, R.drawable.pet_channel);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] profile = os.toByteArray();

                values.put("profile", profile);
                db.insert("Channel", null, values);
                values.clear();


                if(i==0) {
                    values.put("channelId", i);
                    values.put("content", html1);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html2);
                    db.insert("Content", null, values);
                    values.clear();
                }
                else if(i==1) {
                    values.put("channelId", i);
                    values.put("content", html3);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html4);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==2) {
                    values.put("channelId", i);
                    values.put("content", html5);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html6);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==3) {
                    values.put("channelId", i);
                    values.put("content", html7);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html8);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==4) {
                    values.put("channelId", i);
                    values.put("content", html9);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html10);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==5) {
                    values.put("channelId", i);
                    values.put("content", html11);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html12);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==6) {
                    values.put("channelId", i);
                    values.put("content", html13);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html14);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==7) {
                    values.put("channelId", i);
                    values.put("content", html15);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html16);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==8) {
                    values.put("channelId", i);
                    values.put("content", html17);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html18);
                    db.insert("Content", null, values);
                    values.clear();
                }else if(i==9) {
                    values.put("channelId", i);
                    values.put("content", html19);
                    db.insert("Content", null, values);
                    values.clear();

                    values.put("channelId", i);
                    values.put("content", html20);
                    db.insert("Content", null, values);
                    values.clear();
                }

                Cursor cursorContent = db.query("Content", null, "channelId = "+i, null, null, null, null);
//                Log.e("content insert size",""+cursorContent.getCount());
                cursorContent.close();



            }
//            db.close();
        }
        cursor.close();
        db.close();
    }

    public int getChannelNum()
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, null, null, null, null, null);

        int channelNum = cursor.getCount();
        cursor.close();
        db.close();
        return channelNum;
    }

    public void visit(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("visitNum"))+1;
        cursor.close();;
        if(num!=-1) {

            ContentValues values = new ContentValues();
            values.put("visitNum", num);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});
            db.close();
        }
    }

    public void subscribe(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
        cursor.close();;
        if(num==0) {

            ContentValues values = new ContentValues();
            values.put("subscriptionState", 1);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});

        }
        db.close();
    }

    public void unsubscribe(int i)
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("Channel", null, "channelId ="+i, null, null, null, null);
        int num =-1;
        if(cursor.moveToFirst())
            num = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
        cursor.close();;
        if(num==1) {

            ContentValues values = new ContentValues();
            values.put("subscriptionState", 0);
            db.update("Channel", values, "channelId = ?", new String[]{""+i});
        }
        db.close();
    }

//    public String getEmail()
//    {
//
//    }

    public void update(String userEmail)
    {
        ArrayList<DiscoveryChannel> channels = getChannels();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        DatabaseReference channelRef = ref.child("DiscoveryChannel");

        for(int i=0;i<channels.size();i++)
        {
            channelRef = ref.child("DiscoveryChannel").child(""+i);

            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("channelId", channels.get(i).getChannelId());
            updates.put("channelNameId", "Discovery Channel "+i);

            Bitmap bmp = channels.get(i).getProfile();
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            bmp.recycle();
            byte[] byteArray = bYtE.toByteArray();
            String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

            updates.put("profile", imageFile);
            channelRef.updateChildren(updates);
        }


        DatabaseReference contentRef = ref.child("DiscoveryContent");
        for(int i=0;i<channels.size();i++)
        {
            ArrayList<String> contents = channels.get(i).getContents();
            for(int a =0; a<contents.size(); a++) {
                contentRef = ref.child("DiscoveryContent").child("" + i+" "+a);

                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("channelId", channels.get(i).getChannelId());
                updates.put("content", contents.get(a));

                contentRef.updateChildren(updates);
            }
        }


        DatabaseReference recordRef = ref.child("DiscoveryRecord");
        for(int i=0;i<channels.size();i++)
        {
            recordRef = ref.child("DiscoveryRecord").child(""+i);

            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("channelId", channels.get(i).getChannelId());
            updates.put("userEmail", userEmail);

            updates.put("visitNum", channels.get(i).getVisitNum());
            updates.put("subscriptionState", channels.get(i).isSubscriptionState());
            recordRef.updateChildren(updates);
        }
    }

    public ArrayList<DiscoveryChannel> getChannels()
    {
        SnapChatOpenHelper dbHelper = new SnapChatOpenHelper(this.mContext, "snap_chat", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<DiscoveryChannel> channels = new ArrayList<DiscoveryChannel>();

        Cursor cursor = db.query("Channel", null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            do{

                int channelId = cursor.getInt(cursor.getColumnIndex("channelId"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int visitNum = cursor.getInt(cursor.getColumnIndex("visitNum"));
                int subscriptionState = cursor.getInt(cursor.getColumnIndex("subscriptionState"));
                byte[] profile = cursor.getBlob(cursor.getColumnIndex("profile"));
                ArrayList<String> contents = new ArrayList<String>();

                Cursor cursorContent = db.query("Content", null, "channelId = "+channelId, null, null, null, null);
                if(cursorContent.moveToFirst())
                {
//                    Log.e("content size",""+cursorContent.getCount());
                    do{
                        String content = cursorContent.getString(cursorContent.getColumnIndex("content"));
                        contents.add(content);
                    }while(cursorContent.moveToNext());
                }

                DiscoveryChannel channel = new DiscoveryChannel(channelId, name, visitNum, subscriptionState, profile, contents);
                channels.add(channel);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return channels;
    }
}
