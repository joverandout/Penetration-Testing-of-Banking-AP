# Cyber Security Penetration Testing of Wondough Bank API

Joe Moore (u1917702) coursework for CS263 cyber security December 2020.

## Result
Grade awarded: 87%.

My mark Breakdown: 
  > Penetration Testing = 26/30 \
  > Quality Assurance = 24/30 \
  > Mitigations = 28/30 \
  > Good Academic Practice = 9/10

Yeargroup marks breakdown:
  > Marked: 53\
  > Average Mark: 67.58%\
  > Standard Deviation: 21.78%

## The Task

<strong><em>After Wondough Bank’s recent experience with consultants, they are concerned that
the security of a system built by an intern may have inadequate security at best. They
have hired a number of security experts (one of whom is you) to perform a security
audit and penetration test the application, fix the security vulnerabilities that are
identified, and ensure that your fixes adequately address the vulnerabilities that you
have identified
  
Principally, there are three tasks for you to complete. Each task is described in detail
below and is worth 30% of the overall mark for this coursework:
1. Penetration test the banking system to identify vulnerabilities in the software.
2. Set up automated tests for the vulnerabilities that you have identified.
3. Fix the security vulnerabilities that you identified.
The remaining 10% of the coursework mark will be awarded for good academic practice. All four marking criteria are described in detail below.
  
  
There are two deliverables that you need to supply:
  
1. A report of at least 3000-4000 words (approximately 6-8 pages) which documents what you have done. This word/page count is merely a suggestion, not a hard limit. The length of the report should, however, be appropriate for the content: overly verbose reports that do not have much substance and exceed the suggested limits may not be read in full at the discretion of the marker.
2. An archive containing your modified version of the banking system, including
test scripts.</em></strong>

## Running My Submission

To run the code and see the results of the automated tests first start the server for the banking system: In order to start the server for the banking system,
navigate to the `wondough` folder and run the `./gradlew` run command. This will start a web server which listens on a random port. Note that you will get some errors
related to SLF4J which you can ignore and that Gradle will “get stuck” on 75% – this is expected and the server will be running at that point until you press Ctrl+C. The output of the tests should be given when the server starts up. 

![tests](https://media.discordapp.net/attachments/192724811594596352/915212914531663872/unknown.png)

# Disclosure
The code in this git repository that is not part of the git repository `github.com/wondough/api` is the copyright of Joe Moore and distribution or use is not allowed without explicit permission and without giving full credit
