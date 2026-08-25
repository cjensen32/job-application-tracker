# Annotated Resume for the FNBO Interview

**Interview:** Thursday, 2026-08-27, in person, with Arlene (FNBO)
**Role:** Analyst II, Banking Automation Engineer, job number R-20261143
**Posted base range:** $63,288 - $104,425. Hybrid, 3 days in office, 2 remote.
**Referral:** Owen McGrath, passed along through a mutual coworker.
**Applied with:** the general resume (`resumes/onepage.md` / `ConnorJensen_Resume_2026.pdf`), not the tailored FNBO version in `resume.md` in this folder.

This file is the resume Arlene is holding, with a `<details>` block under each
section explaining what that content means *for this job*. Since the submitted
resume was not tailored, the tailoring has to happen out loud in the room. Every
block below is the bridge from a generic bullet to a line in the posting.

Nothing here invents experience. Everything claimed is already in
`ConnorJensen_Resume_2026.txt`, `cover_letter.txt`, or `application_questions.md`,
which is what keeps the paper and the conversation consistent.

<details>
<summary>Read this first: the frame for the whole conversation</summary>

**The posting in one sentence:** find manual, repetitive work inside Risk,
Operations, Finance, and Treasury, and replace it with Python, RPA (Blue Prism),
and AWS automation that survives audit.

**Your one sentence back:** "Most of what I have built is taking a manual process
apart and replacing it with something that runs on its own, in environments where
the change had to be documented and reviewed."

**The three things that have to land:**
1. Python automation is a real hands-on skill, not a coursework line.
2. Regulated-environment work is normal to you, not a new constraint.
3. You do not have Blue Prism, you say so plainly, and you show the underlying
   skill it sits on top of.

**The two gaps to name before they get discovered:** no RPA platform experience,
and the bachelor's is in progress (expected May 2027). Both are already disclosed
in writing, so naming them costs nothing and buys credibility.

**Who Arlene is:** not recorded anywhere in this repo. Do not assume recruiter or
hiring manager. Open by asking what she does at FNBO and how she works with the
automation team, then calibrate depth from her answer. If she is non-technical,
lead with the process outcome (someone got their time back) and keep the stack as
supporting detail. If she is technical, go straight to the tooling.

**Availability:** Shyft ended 2026-08-14, so you are available immediately. Say
"wrapped up," not "currently."
</details>

---

## Header

**Connor Jensen**
Omaha, NE
[LinkedIn – cojensen32](https://www.linkedin.com/in/cojensen32/)
[GitHub – cjensen32](https://github.com/cjensen32)
[Phone - Mobile](tel:+14022176642)
[Email - Personal](cojensen32@gmail.com)

<details>
<summary>Header: why "Omaha, NE" is doing more work than it looks</summary>

This is a hybrid role, 3 days in office. Being local with no relocation and no
sponsorship need removes three of the quiet risks a recruiter screens for.
Work authorization is a stated requirement on this posting and you meet it as a
U.S. citizen.

The Omaha angle is also a genuine answer to "why FNBO," and it is already the
closing line of your application form answer: doing this kind of engineering at a
bank headquartered in the city you live in. Say it the same way you wrote it.

Owen McGrath belongs in the first minute. Attribute it accurately: a coworker
flagged the role and connected you to Owen, who referred you. Do not imply a
personal relationship with Owen, because Arlene may know him.
</details>

## Professional Summary

Full stack developer with 18 months of professional experience building and maintaining production applications using Python, React, and TypeScript. Experienced in cloud migration, secure coding, vulnerability remediation, and Agile development within a government contracting environment. Security+ certified and currently completing a bachelor's degree in computer science.

<details>
<summary>Summary: the one line on the page that is aimed at the wrong job</summary>

"Full stack developer" and "React, TypeScript" lead this summary. For this
posting that is the least relevant framing of your experience, and it is what
Arlene read first. Correct it verbally, early, without disowning it:

> "The resume I sent leads with full stack because that is the broad version.
> For this role the more relevant half is the Python side: automated data
> processing, tooling that replaced manual work, and deploying it to AWS."

The tailored summary in `resume.md` in this folder is the sentence to have in
your head: Python automation and data-processing tooling, plus prior lab work
automating repetitive manual research workflows, deployed to AWS, written under
change management and compliance review.

**"18 months"** maps directly to the posting's Required band of 1-3+ years, and
adding roughly two years at the Koraleski CAB Lab puts you inside the band rather
than under it. Count the lab work out loud, it is technical work, not a
student job.

**"Government contracting environment"** is the phrase to slow down on. The
posting says "regulated banking environment," internal controls, audit
requirements, regulatory expectations. Your honest analog: controlled change,
documented rationale, auditability. Frame it as the same *shape* of work, never
as equivalent banking domain knowledge.

**"Currently completing a bachelor's"** is the disclosed gap against a Required
bachelor's. Expected May 2027, in progress everywhere, never implied complete.
If it comes up: state the date, note the 18 months of professional work already
behind you, and move on. Do not apologize through it.
</details>

## Certifications

CompTIA Security+ (SY0-701), July 2026
Security Clearance: Secret

<details>
<summary>Certifications: Security+ is relevant here, the clearance is not</summary>

**Security+** matters for this job in a specific way. The posting asks for
"secure access and integration with internal systems while adhering to enterprise
security and compliance standards." Banking automation touches production data
with real customer exposure, so a candidate who already thinks about least
privilege, credential handling, and dependency risk is lower risk to hand an
automation account to. Pair the cert with the real story: you found and fixed
several vulnerabilities in a production frontend and verified each fix did not
break anything.

**The Secret clearance is not a selling point here.** This role requires no
clearance. Keep it as a one-line credential only. If it comes up, the useful
framing is that it means a completed federal background investigation, which is
not nothing at a bank, and then move on. Do not lead with it and do not let it
eat conversation time that Python automation should get.

Note the resume Arlene has lists the clearance *above* Security+. For this room,
that ordering is backwards. Another reason to steer.
</details>

## Skills

**Languages:** Python, TypeScript, JavaScript, C, Java
**Frameworks & Tools:** Flask, FastAPI, React, Vite, MUI, MongoDB
**Cloud & DevOps:** AWS (EC2/AMI), Docker, Kubernetes, nginx, GitHub Actions, GitLab, Bitbucket
**Practices:** Agile development, secure coding, vulnerability remediation, CI/CD, cross-functional collaboration

<details>
<summary>Skills: what the PDF version actually says, and the SQL question</summary>

**Check which copy she has.** The submitted PDF's skills line is slightly longer
than the markdown above. It reads: "Flask, FastAPI, React, Vite, MUI, MongoDB,
Postgres, SQLite, Spring Boot (coursework)" and adds Git to the Cloud & DevOps
line. So Postgres and SQLite *are* on the page in front of her. Do not be
surprised by a SQL question.

**SQL is the honest soft spot.** The posting asks for "basic to intermediate SQL
skills for querying and validating data," which is a Required line. Your position
on paper, deliberately, is that databases show up as systems you built on rather
than as a proficiency claim. Hold that line in the room:

> "The systems I worked on were backed by Postgres, SQLite, and MongoDB. I wrote
> queries against them as part of building and validating features. I would call
> that working SQL rather than deep SQL, and joins and aggregates are where I am
> solid."

Do not claim intermediate SQL you cannot demonstrate. Do brush up joins,
`GROUP BY`, and a basic subquery before Thursday, since "validate outputs against
source systems" is literally a job responsibility and a whiteboard query is a
plausible ask.

**AWS: know exactly what you have and what you do not.**
- Have: EC2/AMI deployment, a real on-prem to AWS VPC migration, Kubernetes and
  Docker through that migration, deployed a split backend and frontend pair
  yourself.
- Do not have: Lambda, S3, EventBridge, Step Functions, Snowflake, or any
  workflow orchestrator (Airflow, Control-M).

The posting's Required line only asks for "foundational experience or exposure to
AWS," which you clear. The named services live in Responsibilities and Desired.
The right answer when Lambda or Step Functions comes up is the conceptual one plus
the honest boundary: you understand event-driven and scheduled execution as
concepts, you have not shipped a Lambda, and the closest thing you have done is
deploying and operating services on EC2 inside a VPC. Then point at the learning
track record (Kubernetes and Grib2/NetCDF both picked up on the job).

**What is missing from this skills list that should be said out loud:**
Pandas, NumPy, Matplotlib, and VBA. Those are the automation and data-processing
tools this job actually cares about, and they are on the tailored resume but not
on the copy she has.

**Java and C:** listed, but do not volunteer them. Java on the page can pull the
conversation toward FNBO's Java Developer posting, which is a worse fit than
this one.
</details>

## Experience

### Software Engineer Intern
**Shyft Solutions** | Omaha, NE
**March 2025 – August 2026**

- Helped move a legacy on-prem product into an AWS VPC as part of a multi-team migration, working in Kubernetes and Coder-based dev environments.
- Stabilized and modernized a React/TypeScript frontend (Vite, MUI, ESLint, Prettier, Vitest) while the migration was still in progress.
- Found and fixed several security vulnerabilities in a production frontend, mostly deprecated dependencies. Pulled fixes from open-source issue threads and verified nothing broke.
- Built Python tools for weather data processing and visualization, including Skew-T plot generation and Grib2/NetCDF handling, using Flask, Matplotlib, and MongoDB. Demoed them live to internal stakeholders over Teams.
- Ran a multi-month project on my own to split a combined Flask+React monorepo into separate backend and frontend repos, then deployed both to AWS. Handed it off to incoming interns who finished it.
- Wrote code under government-contract change management and sat in on compliance reviews. Worked across two 10-person Agile teams using Jira for tickets, Bitbucket and GitLab for version control, and Confluence for docs.

<details>
<summary>Shyft: reorder these bullets when you talk, the page order is wrong for FNBO</summary>

On the page, bullet order is migration, frontend, security, Python tooling,
monorepo split, compliance. **Speak them in this order instead:** Python tooling,
monorepo split and AWS deploy, compliance and change management, AWS VPC
migration, security fixes, frontend last or not at all.

**Bullet 4, the Python tooling, is your strongest bullet for this job.** It is
data processing and visualization, which is nearly the posting's phrasing
("build automation scripts to support data processing, reporting workflows").
Details to add out loud that are not on the page: Pandas and NumPy alongside
Matplotlib, Grib2 and NetCDF as binary scientific file formats you had never
touched before and learned on the job, and the fact that you demoed it live to
internal stakeholders over Teams. That last part answers "collaborate effectively
with technical and non-technical stakeholders" without you having to claim it.

**Bullet 5, the monorepo split, is the ownership story.** Multi-month, run
independently, ended with both halves deployed to AWS and a clean handoff to
incoming interns who finished it. Two things to add verbally: it included a
SQLite-backed employee training-status tracker (that is your relational data and
your "I built the thing that tracks the thing" example), and the handoff is
evidence of documentation quality, which the posting asks for under "document
automation logic, workflows, and AWS architecture for maintainability."

**Bullet 6 is the regulated-environment bullet.** Do not let it stay this
compressed in conversation. Expand it into what the process actually felt like:
changes going through review before they landed, having to justify why a change
was made and not just what it did, sitting in compliance reviews. Then draw the
line yourself: "That is not banking regulation, and I would be learning FNBO's
controls from scratch. But building inside a control process is not new to me."
Naming the limit is what makes the analogy land instead of sounding oversold.

**Bullet 1, the VPC migration, is your AWS credibility.** Add the detail from the
tailored version: the legacy product was a Postgres and MongoDB backed
request-tracking system. That turns a vague "cloud migration" into a system with
a shape, and it is another relational data touchpoint.

**Bullet 3, the security fixes, pairs with Security+.** Good answer to "tell me
about a time you had to be careful." The verification half matters more than the
finding half here: you pulled fixes from open-source issue threads and confirmed
nothing broke. That is the same instinct as "validate outputs against source
systems and business expectations," which is a listed responsibility.

**Bullet 2, the frontend, is the least relevant line on the page for this role.**
One sentence if asked, then steer back. Do not spend interview minutes on MUI.

**Title question you should expect:** "Software Engineer Intern" for 18 months.
Have the plain answer ready. It was a paid engineering role alongside school, you
worked on two 10-person Agile teams, and you were shipping production code under
change management, not shadowing.
</details>

### Senior Technology Intern
**Koraleski CAB Lab** | Omaha, NE
**September 2022 – November 2024**

- Promoted twice in two years, from Proctor up to Technology Intern, taking on more technical work each time. Built a Python/VBA tool called iMotions-DataCleaner that automated Excel data cleaning for research prep.
- Ran data collection, prep, and analysis for lab studies. Also redesigned the lab website around usability and accessibility.
- Monitored live study sessions with eye tracking, GSR, heart rate, and EDA equipment. Presented lab capabilities to visiting groups, including Union Pacific and the University of Nebraska Board of Regents.

<details>
<summary>Koraleski: the single best story you have for this specific job</summary>

This entry looks like a student lab job on paper. For this posting it is the most
on-point experience you own, and if only one story gets told well on Thursday,
make it this one.

**iMotions-DataCleaner is the answer to the RPA question.** A human being was
cleaning research data in Excel by hand, repeatedly. You looked at that process,
decomposed it into repeatable steps, and built a Python and VBA tool that took it
over. That is exactly the posting's "identify opportunities to eliminate manual,
repetitive tasks across reporting and operational processes," and it happens to
be Excel-driven manual work, which is precisely what Blue Prism gets pointed at
inside a bank.

Have the full arc ready in STAR shape:
- **Situation:** research prep required cleaning exported Excel data by hand
  before analysis could start.
- **Task:** you noticed the repetition and proposed automating it.
- **Action:** Python plus VBA, built against the real files, iterated with the
  people who actually did the cleaning.
- **Result:** the manual step went away and prep time went back to the
  researchers.

Be ready for the follow-ups you should already know cold, since this is your own
code: how you handled malformed or unexpected input, how you knew the output was
correct, whether anyone else could run it after you left. If the honest answer to
one of those is "not as well as I would do it now," say that. Naming what you
would build differently today is a strength signal, not a weakness.

**"Promoted twice in two years"** is worth saying plainly. It is external
evidence of trust and increasing responsibility, from Proctor to Technology
Intern to Senior Technology Intern.

**The presenting-to-visitors bullet is not filler here.** Union Pacific and the
Board of Regents means you have explained technical work to non-technical
audiences who outranked you. The posting asks for exactly that ability, twice.
It also supports "partner with stakeholders to improve process design prior to
automation," which is a people skill, not a coding one.

**The eye tracking, GSR, and EDA equipment detail** is on the resume but not in
the cover letter, so expect it cold as a curiosity question. Short honest answer:
you ran live study sessions on that instrumentation and handled the data it
produced, which is where the data-cleaning problem came from in the first place.
That connects the "quirky" bullet back to automation instead of leaving it as
trivia.
</details>

## Education

### Bachelor's in Computer Science
**University of Nebraska Omaha** | Omaha, NE
**August 2022 – Present**
Expected: May 2027
GPA: 3.6

<details>
<summary>Education: the Required-degree gap, and what offsets it</summary>

The posting lists a bachelor's under **Required** and yours is in progress. This
is the second of the two disclosed gaps. It has been stated as in-progress in
every document submitted, so there is nothing to walk back.

How to handle it if raised: give the date (May 2027), note that you have been
working professionally for 18 months while enrolled, and let the 3.6 GPA sit
there as evidence you carried both. Do not volunteer it unprompted, and do not
over-explain when it comes up.

Worth having ready but not on this page: the concentration is Software
Engineering, and Dean's List in Fall 2022. Relevant coursework includes Data
Structures and Object-Oriented Software Engineering Fundamentals.

**FNBO offers tuition assistance**, which is listed in the posting's benefits.
Asking about it is a legitimate, non-awkward question that turns the degree gap
into a reason you want to work there. It signals you intend to finish and to
stay.
</details>

---

<details>
<summary>The Blue Prism question: the one you will definitely get</summary>

Blue Prism appears three times in the posting, in Responsibilities, in
Knowledge/Skills, and in Desired. You have never used Blue Prism, UiPath,
Automation Anywhere, or anything comparable, and you have not claimed otherwise
anywhere. Your cover letter already disclosed this, so Arlene may open with it.

The answer, in the same shape the letter made it:

> "I have not worked in an RPA platform like Blue Prism. What I have done is the
> work underneath it: taking a manual process apart, turning it into repeatable
> steps, and building something that runs reliably without a person in the loop.
> The clearest example is a tool I built that replaced Excel data cleaning that
> had been done by hand. I picked up Kubernetes and binary weather-data formats
> on the job at Shyft with no prior exposure, and I expect Blue Prism to go the
> same way."

**Rules for this answer:** do not overclaim familiarity if pressed. Do not say
"I have read about it" as if that were experience. It is fine and better to say
you have not looked at it in depth yet. What you are promising is learning speed,
not existing fluency, and that distinction is the entire reason the answer is
credible.

If she asks whether you have looked at it since applying, the honest answer is
whatever is true on Thursday. Spending an hour on what Blue Prism actually is
(attended vs unattended bots, process vs object layer, why banks use it for
systems that have no API) is reasonable prep and lets you ask a real question
about how FNBO uses it.
</details>

<details>
<summary>Questions to ask Arlene</summary>

- What does she do at FNBO, and how does her team work with the automation group?
  (Ask early, it calibrates everything else.)
- Which business areas send the most automation requests, Risk, Operations,
  Finance, or Treasury, and what does a typical intake look like?
- How is the split between Blue Prism work and straight Python or AWS automation
  on this team? Is it roughly even, or is one the default?
- What does the path from request to production look like, specifically the
  control and audit steps? (This shows you expect controls rather than resent
  them.)
- Who does an Analyst II pair with day to day, and what does support from the
  Senior Banking Automation Engineers actually look like?
- How does the 3/2 hybrid split work in practice for this team, are the in-office
  days fixed?
- What does FNBO's tuition assistance look like for someone finishing a degree
  while working?
- What are the next steps and the timeline?
</details>

<details>
<summary>Two-day prep plan (Tuesday and Wednesday)</summary>

**Highest value, do these:**
1. Rehearse the iMotions-DataCleaner story out loud until it runs about 90
   seconds without notes, including the follow-ups about correctness and
   handoff. This is the story that wins the interview.
2. Rehearse the Blue Prism answer out loud. Get comfortable saying "I have not
   used it" without flinching, because the flinch is what reads badly, not the
   gap.
3. Re-read `cover_letter.txt` and `application_questions.md` in this folder.
   Arlene may have both. Your spoken answers should match them, not contradict
   them.
4. One SQL refresher pass: inner and left joins, `GROUP BY` with aggregates, one
   subquery. Enough to write a small query on request.

**Worth it if there is time:**
5. Thirty to sixty minutes on what Blue Prism is conceptually, purely so you can
   ask an informed question about how FNBO uses it.
6. Skim the AWS gap list so Lambda, S3, EventBridge, and Step Functions do not
   catch you flat. Conceptual understanding plus an honest boundary is a complete
   answer at this level.
7. Look up FNBO basics: founded 1857, one of the largest privately held banks in
   the country, headquartered in Omaha. Enough to sound like you looked, not a
   recital.

**Logistics:** confirm the building, floor, and parking the day before. In person
means arrive early. Bring printed copies of the resume she already has, since the
version you hand over should match the version she read.

**One thing not to do:** do not hand over or reference the tailored `resume.md`
in this folder as if it were what you submitted. It contains the same facts in a
better order, so use it to prepare what you say, not as a second document that
raises a which-one-is-real question.
</details>

<details>
<summary>Repo housekeeping (not for the interview)</summary>

`job_profile.md` in this folder still says **Status: Not yet applied**. An
interview has now been scheduled, so that line is stale. Worth updating to
reflect applied, plus the interview date, so the next session working from these
files starts with accurate state.
</details>
