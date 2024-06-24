import { Flex, Text } from "@chakra-ui/react";

const Banner: React.FC = () => {
	return (
		<Flex bg='green.400' alignItems='center' justifyContent='center' h='500px'>
			<Flex gap='64px'>
				<Text textAlign='end' fontSize='4xl' fontWeight='bolder' color='white'>
					whoEver<br />
					whenEver<br />
					wherEver
				</Text>
				<Text fontSize='4xl' fontWeight='bolder' color='white'>
					누구나<br />
					언제든<br />
					어디서든<br />
					<br />
					실력을 기르고 지식을 나눠보세요
				</Text>
			</Flex>
		</Flex>
	)
}

export default Banner
