note
	description: "A sequence of pseudorandom numbers."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	RANDOMNUMBERGENERATOR

create
	make

feature --i Initialization
	make
			-- create a new instance seeded by time
		do
			set_seed
		end

feature
	new_random: INTEGER
			-- Returns the next pseudorandom integer.
		do
			random_sequence.forth
			Result := random_sequence.item
		end

feature {NONE}
	random_sequence: RANDOM

feature
	set_seed
			-- initialize the random_sequence based on the milliseconds since midnight
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
