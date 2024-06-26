note
	description: "Summary description for {RANDOMNUMBERGENERATOR}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	RANDOMNUMBERGENERATOR

create
	make

feature --i Initialization
	make
		do
			set_seed
		end

feature
	new_random: INTEGER
    	-- Random integer
    	-- Each call returns another random number.
  	do
    	random_sequence.forth
    	Result := random_sequence.item
  	end

feature
	get_random: INTEGER
		--this method returns a semi-random 2 digit number based on the seconds passed since midnight
		local
			l_time: TIME
			l_seed: INTEGER
		do
			-- This computes milliseconds since midnight.
			create l_time.make_now
			l_seed := l_time.hour
			l_seed := l_seed * 60 + l_time.minute
			l_seed := l_seed * 60 + l_time.second
			l_seed := l_seed * 1000 + l_time.milli_second
			l_seed := l_seed * 1000 + l_time.micro_second
			create random_sequence.set_seed (l_seed)
			Result := l_seed\\100 --\\ is modulo, this returns the last 2 digits of seed
		end

feature {NONE}
	random_sequence : RANDOM

feature
	set_seed
	    local
      		l_time: TIME
      		l_seed: INTEGER
    	do
         	-- This computes milliseconds since midnight.

      		create l_time.make_now
      		l_seed := l_time.hour
      		l_seed := l_seed * 60 + l_time.minute
      		l_seed := l_seed * 60 + l_time.second
      		l_seed := l_seed * 1000 + l_time.milli_second
      		create random_sequence.set_seed (l_seed)
    	end

end
