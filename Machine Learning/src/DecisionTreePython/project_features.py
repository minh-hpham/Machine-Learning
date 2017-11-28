def featurize(fname):


    with open(fname) as f:
        data = [line.rstrip() for line in f]
        
    new_data = []

    for d in data:
        one = two = three = eight = nine = eleven = sixteen = 0
        d = d.split(" ")
        label = int(d[0])

        a1 = int(d[1].split(":")[1])
        a2 = int(d[2].split(":")[1])
        a3 = int(d[3].split(":")[1])
        a8 = int(d[8].split(":")[1])
        a9 = float(d[9].split(":")[1])
        a11 = float(d[11].split(":")[1])
        a16 = float(d[16].split(":")[1])
        
        if a1 <= 7:
            one = 1
        # Is their first name longer than their last name?
        if a2 <= 9 or a2 >= 160:
            two = 1
        # Does their first name start and end with the same letter? (ie "Ada")
        if a3 >=19 and a3 <261 :
            three = 1
        # Does their first name come alphabetically before their last name? (ie "Dan Klein" because "d" comes before "k")
        if a8 >= 16 and a8 < 81:
            eight= 1
        # Is the second letter of their first name a vowel (a,e,i,o,u)?
        if a9 <= 0.0:
            nine = 1
        # Is the number of letters in their last name even?
        if a11 >= 1.0 and (a11%5)== 0:
            eleven = 1
        
        if a16 < 81:
            sixteen = 1

        new_data.append([[one,two,three,eight,nine, eleven,sixteen], label])

    return new_data