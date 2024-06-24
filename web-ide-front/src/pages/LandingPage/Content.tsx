import ide from '../../assets/images/ide.png';
import chat from '../../assets/images/chat.png';
import share from '../../assets/images/share.png';
import { Box, Button, Center, Flex, Image, Text } from '@chakra-ui/react';
import { useNavigate } from 'react-router-dom';

interface ContentItemProps {
	img: string;
	title: string;
	description: string;
}

const contents: ContentItemProps[] = [
	{
		img: ide,
		title: '실시간 동시 편집 IDE',
		description: '실시간으로 코드를 동시 편집 가능합니다.',
	},
	{
		img: chat,
		title: '실시간 채팅',
		description: '실시간으로 소통하며 코드를 작성할 수 있습니다.',
	},
	{
		img: share,
		title: '질문 및 강의 게시판',
		description: '본인의 IDE 컨테이너를 질문 및 강의 게시판에 공유함으로써 다른 사람들에게 질문하거나 강의를 할 수 있습니다.',
	}
]

interface ContentItemComponentProps {
	item: ContentItemProps;
	index: number;
}

const ContentItem: React.FC<ContentItemComponentProps> = ({ item, index }) => {
	if (index % 2) {
		return (
			<Flex bg='gray.50'>
				<Center p='64px' w='50%'>
					<Box>
						<Text fontSize='4xl' fontWeight='bold' color='blackAlpha.800' >{item.title}</Text>
						<Text fontSize='large' color='blackAlpha.500' >{item.description}</Text>
					</Box>
				</Center>
				<Image
					boxSize='50%'
					src={item.img}
					alt={item.title}
				/>
			</Flex>
		)
	}
	return (
		<Flex>
			<Image
				boxSize='50%'
				src={item.img}
				alt={item.title}
			/>
			<Center p='64px' w='50%'>
				<Box>
					<Text fontSize='4xl' fontWeight='bold' color='blackAlpha.800' >{item.title}</Text>
					<Text fontSize='large' color='blackAlpha.500' >{item.description}</Text>
				</Box>
			</Center>
		</Flex>
	)
}

const Content: React.FC = () => {
	const navigate = useNavigate();

	return (
		<Box>
			<Box>
				{contents.map((item, index) => (
					<ContentItem key={index} item={item} index={index} />
				))}
			</Box>
			<Center m='64px'>
				<Button
					onClick={() => navigate('/login')}
					colorScheme='green'
					borderRadius='32px'
					fontSize='xl'
					p='24px 32px'
				>
					Get Started
				</Button>
			</Center>
		</Box>
	)
}

export default Content