import csv
import random

# Build regNo prefixes pool exactly as requested
prefixes = []
prefixes += [f"EB{i}" for i in range(1,16)]       
prefixes += [f"CB{i}" for i in range(1,17)]      
prefixes += [f"ENG{i}" for i in range(1,11)]    
prefixes += [f"BC{i}" for i in range(1,11)]      

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


course_map = {}
for i, cname in enumerate(courses):
    prefix = prefixes[i % len(prefixes)]  
    course_map[prefix] = cname

# Write courses.csv
with open("courses.csv","w",newline="") as f:
    writer = csv.writer(f)
    writer.writerow(["courseID","name"])
    for cid, cname in course_map.items():
        writer.writerow([cid, cname])
