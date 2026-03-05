import csv
import random

# Define years and semesters
years = [1, 2, 3, 4]
semesters = [1, 2]

# Sample unit names to pick from
unit_names = [
    "Introduction to Programming", "Data Structures", "Algorithms",
    "Database Systems", "Operating Systems", "Computer Networks",
    "Software Engineering", "Artificial Intelligence", "Web Development",
    "Mobile Application Development", "Cyber Security", "Machine Learning",
    "Human Computer Interaction", "Compiler Design", "Distributed Systems"
]

# Build regNo prefixes pool
prefixes = []
prefixes += [f"EB{i}" for i in range(1,16)]
prefixes += [f"CB{i}" for i in range(1,17)]
prefixes += [f"ENG{i}" for i in range(1,11)]
prefixes += [f"BC{i}" for i in range(1,11)]

# Full course list (must match learners script)
courses = [
    "Computer Science","Applied Computer Science","Information Technology","Software Engineering",
    "Electrical Engineering","Mechanical Engineering","Civil Engineering","Chemical Engineering",
    "Biomedical Engineering","Actuarial Science","Mathematics","Statistics","Physics","Chemistry",
    "Biology","Environmental Science","Agricultural Science","Economics","Finance","Accounting",
    "Business Administration","Marketing","Human Resource Management","Political Science","Sociology",
    "Psychology","Philosophy","History","Literature","Education","Nursing","Medicine","Pharmacy",
    "Law","Criminology","Anthropology","Geography","Architecture","Fine Arts","Music","Theatre Arts",
    "Data Science","Artificial Intelligence","Cybersecurity","Cloud Computing","Robotics","Astronomy",
    "Biotechnology","Food Science","Veterinary Medicine","Public Health","Social Work","International Relations"
]

# Map prefixes to courses
course_map = {}
for i, cname in enumerate(courses):
    prefix = prefixes[i % len(prefixes)]
    course_map[prefix] = cname

# Generate course units
rows = []
for courseID in course_map.keys():
    for year in years:
        for semester in semesters:
            num_units = random.randint(5, 9)
            for i in range(1, num_units + 1):
                unit_name = random.choice(unit_names)
                unit_code = f"{courseID}-Y{year}S{semester}U{i:02d}"
                rows.append([unit_name, unit_code, courseID, year, semester])

# Write to CSV
with open("course_units.csv", "w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)
    writer.writerow(["unit_name", "unit_code", "courseID", "year", "semester"])
    writer.writerows(rows)

print("CSV file 'course_units.csv' generated successfully!")
